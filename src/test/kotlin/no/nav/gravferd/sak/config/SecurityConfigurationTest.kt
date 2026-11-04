package no.nav.gravferd.sak.config

import jakarta.servlet.Filter
import no.nav.gravferd.sak.sikkerhet.AzureAdAuthenticationManager
import no.nav.gravferd.sak.sikkerhet.AzureAdJwtAuthenticationConverter
import no.nav.gravferd.sak.sikkerhet.TestRolleConfig.rolleConfig
import no.nav.gravferd.sak.sikkerhet.TestTokenServer.token
import no.nav.gravferd.sak.sikkerhet.TestTokenServerConfig
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringJUnitWebConfig(SecurityConfigurationTest.TestConfig::class)
class SecurityConfigurationTest {
    @Configuration
    @EnableWebMvc
    @Import(
        TestTokenServerConfig::class,
        SecurityConfiguration::class,
        AzureAdAuthenticationManager::class,
        AzureAdJwtAuthenticationConverter::class,
        TestController::class,
    )
    class TestConfig {
        @Bean
        fun rolleConfig() = rolleConfig
    }

    @RestController
    class TestController {
        @GetMapping(
            "/internal/health/liveness",
            "/internal/info",
            "/internal/prometheus",
            "/internal/annet",
            "/v3/api-docs/test",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/api/test",
        )
        fun get() = "ok"

        @PostMapping("/api/test")
        fun post() = "ok"

        @PreAuthorize("hasRole('BESLUTTER')")
        @GetMapping("/api/beslutter")
        fun beslutter() = "ok"
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private lateinit var springSecurityFilterChain: Filter

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc =
            MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters<DefaultMockMvcBuilder>(springSecurityFilterChain)
                .build()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/internal/health/liveness",
            "/internal/info",
            "/internal/prometheus",
            "/v3/api-docs/test",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
        ],
    )
    fun `åpne endepunkter krever ikke token`(sti: String) {
        mockMvc
            .perform(get(sti))
            .andExpect(status().isOk)
    }

    @Test
    fun `beskyttet endepunkt uten token gir 401`() {
        mockMvc
            .perform(get("/api/test"))
            .andExpect(status().isUnauthorized)
            .andExpect(header().string("WWW-Authenticate", startsWith("Bearer")))
    }

    @Test
    fun `internal-endepunkt utenfor whitelist krever token`() {
        mockMvc
            .perform(get("/internal/annet"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `beskyttet endepunkt med ugyldig token gir 401`() {
        val token = token(audience = "annen-app")

        mockMvc
            .perform(get("/api/test").header("Authorization", "Bearer $token"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `beskyttet endepunkt med gyldig token gir 200`() {
        val token = token()

        mockMvc
            .perform(get("/api/test").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `post med gyldig token gir 200 uten csrf-token`() {
        val token = token()

        mockMvc
            .perform(post("/api/test").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `oppretter ikke sesjon ved avvist kall`() {
        mockMvc
            .perform(get("/api/test"))
            .andExpect(status().isUnauthorized)
            .andExpect { assertThat(it.request.getSession(false)).isNull() }
    }

    @Test
    fun `PreAuthorize avviser bruker uten påkrevd rolle`() {
        val token = token(grupper = listOf(rolleConfig.veilederRolle))

        mockMvc
            .perform(get("/api/beslutter").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `PreAuthorize slipper inn bruker med påkrevd rolle`() {
        val token = token(grupper = listOf(rolleConfig.beslutterRolle))

        mockMvc
            .perform(get("/api/beslutter").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
    }
}
