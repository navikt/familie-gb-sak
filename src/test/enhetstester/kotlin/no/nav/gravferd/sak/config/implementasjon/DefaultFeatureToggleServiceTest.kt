package no.nav.gravferd.sak.config.implementasjon

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.familie.unleash.UnleashService
import no.nav.gravferd.sak.config.sikkerhet.settSecurityHolderContext
import no.nav.gravferd.sak.kjerne.featuretoggle.FeatureToggle
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.core.context.SecurityContextHolder

class DefaultFeatureToggleServiceTest {
    private val toggle = mockk<FeatureToggle>()
    private val unleashService = mockk<UnleashService>()
    private val featureToggleService = DefaultFeatureToggleService(unleashService)

    @BeforeEach
    fun setup() {
        every { toggle.navn } returns "test-toggle"
        every { unleashService.isEnabled(any(), any<Map<String, String>>()) } returns false
    }

    @AfterEach
    fun ryddOpp() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `erPå sender navIdent og epost fra innlogget saksbehandler`() {
        // Arrange
        settSecurityHolderContext()

        // Act
        featureToggleService.erPå(toggle)

        // Assert
        verify {
            unleashService.isEnabled(
                toggleId = "test-toggle",
                properties =
                    mapOf(
                        "navIdent" to "Z123456",
                        "epost" to "ola.nordmann@nav.no",
                    ),
            )
        }
        confirmVerified(unleashService)
    }

    @Test
    fun `erPåForBehandling sender behandlingId i tillegg til saksbehandler`() {
        // Arrange
        settSecurityHolderContext()

        // Act
        featureToggleService.erPåForBehandling(toggle, 42L)

        // Assert
        verify {
            unleashService.isEnabled(
                toggleId = "test-toggle",
                properties =
                    mapOf(
                        "behandlingId" to "42",
                        "navIdent" to "Z123456",
                        "epost" to "ola.nordmann@nav.no",
                    ),
            )
        }
        confirmVerified(unleashService)
    }

    @Test
    fun `erPåForFagsak sender fagsakId i tillegg til saksbehandler`() {
        // Arrange
        settSecurityHolderContext()

        // Act
        featureToggleService.erPåForFagsak(toggle, 7L)

        // Assert
        verify {
            unleashService.isEnabled(
                toggleId = "test-toggle",
                properties =
                    mapOf(
                        "fagsakId" to "7",
                        "navIdent" to "Z123456",
                        "epost" to "ola.nordmann@nav.no",
                    ),
            )
        }
        confirmVerified(unleashService)
    }

    @Test
    fun `uten innlogget bruker sendes systemverdi`() {
        // Act
        featureToggleService.erPå(toggle)

        // Assert
        verify {
            unleashService.isEnabled(
                toggleId = "test-toggle",
                properties =
                    mapOf(
                        "navIdent" to "VL",
                        "epost" to "VL",
                    ),
            )
        }
        confirmVerified(unleashService)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `returnerer svaret fra Unleash`(svar: Boolean) {
        // Arrange
        every { unleashService.isEnabled(any(), any<Map<String, String>>()) } returns svar

        // Act
        val erPå = featureToggleService.erPå(toggle)
        val erPåForBehandling = featureToggleService.erPåForBehandling(toggle, 1L)
        val erPåForFagsak = featureToggleService.erPåForFagsak(toggle, 1L)

        // Assert
        assertThat(listOf(erPå, erPåForBehandling, erPåForFagsak)).containsOnly(svar)
    }

    @Test
    fun `innlogget bruker uten NAVident gir feil`() {
        // Arrange
        settSecurityHolderContext(claims = mapOf("preferred_username" to "ola.nordmann@nav.no", "name" to "Ola Nordmann"))

        // Act
        val feil = catchThrowable { featureToggleService.erPå(toggle) }

        // Assert
        assertThat(feil).isInstanceOf(IllegalStateException::class.java)
        verify(exactly = 0) { unleashService.isEnabled(any(), any<Map<String, String>>()) }
    }
}
