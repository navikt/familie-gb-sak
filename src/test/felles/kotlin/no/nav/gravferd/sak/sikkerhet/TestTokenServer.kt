package no.nav.gravferd.sak.sikkerhet

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.DynamicPropertyRegistrar
import org.springframework.test.context.DynamicPropertyRegistry

object TestTokenServer {
    const val DEFAULT_SUBJECT = "test-bruker"
    const val DEFAULT_AUDIENCE = "test-client-id"
    private const val AZUREAD_ISSUER_ID = "azuread"
    private const val DEFAULT_CLIENT_ID = "test-azp"

    private val mockOAuth2Server: MockOAuth2Server =
        MockOAuth2Server().also {
            it.start()
            Runtime.getRuntime().addShutdownHook(Thread { it.shutdown() })
        }

    val issuer: String = mockOAuth2Server.issuerUrl(AZUREAD_ISSUER_ID).toString()
    val jwksUri: String = mockOAuth2Server.jwksUrl(AZUREAD_ISSUER_ID).toString()

    fun registrerProperties(registry: DynamicPropertyRegistry) {
        registry.add("AZURE_OPENID_CONFIG_ISSUER") { issuer }
        registry.add("AZURE_OPENID_CONFIG_JWKS_URI") { jwksUri }
        registry.add("AZURE_APP_CLIENT_ID") { DEFAULT_AUDIENCE }
    }

    fun token(
        grupper: List<String>? = emptyList(),
        subject: String = DEFAULT_SUBJECT,
        audience: String = DEFAULT_AUDIENCE,
        ekstraClaims: Map<String, Any> = emptyMap(),
        expiry: Long = 3600,
    ): String =
        mockOAuth2Server
            .issueToken(
                AZUREAD_ISSUER_ID,
                DEFAULT_CLIENT_ID,
                DefaultOAuth2TokenCallback(
                    issuerId = AZUREAD_ISSUER_ID,
                    subject = subject,
                    audience = listOf(audience),
                    claims =
                        buildMap {
                            grupper?.let { put("groups", it) }
                            put("azp", DEFAULT_CLIENT_ID)
                            put("NAVident", "Z999999")
                            putAll(ekstraClaims)
                        },
                    expiry = expiry,
                ),
            ).serialize()
}

@Configuration
class TestTokenServerConfig {
    @Bean
    fun mockOAuth2ServerProperties() = DynamicPropertyRegistrar { TestTokenServer.registrerProperties(it) }
}
