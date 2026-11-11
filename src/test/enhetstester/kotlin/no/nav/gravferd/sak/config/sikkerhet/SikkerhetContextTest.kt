package no.nav.gravferd.sak.config.sikkerhet

import no.nav.gravferd.sak.kjerne.tilgang.Rolle
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

class SikkerhetContextTest {
    @AfterEach
    fun ryddOpp() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `tom sikkerhetscontext er systemkontekst`() {
        // Act
        val saksbehandler = SikkerhetContext.hentSaksbehandler()
        val epost = SikkerhetContext.hentSaksbehandlerEpost()
        val navn = SikkerhetContext.hentSaksbehandlerNavn()
        val jwt = SikkerhetContext.hentJwt()
        val erSystemKontekst = SikkerhetContext.erSystemKontekst()
        val harRolle = SikkerhetContext.harRolle(Rolle.SAKSBEHANDLER)

        // Assert
        assertThat(saksbehandler).isEqualTo("VL")
        assertThat(epost).isEqualTo("VL")
        assertThat(navn).isEqualTo("System")
        assertThat(jwt).isNull()
        assertThat(erSystemKontekst).isTrue()
        assertThat(harRolle).isFalse()
    }

    @Test
    fun `anonym bruker er ikke systemkontekst`() {
        // Arrange
        SecurityContextHolder.getContext().authentication =
            AnonymousAuthenticationToken("nøkkel", "anonymousUser", listOf(SimpleGrantedAuthority("ROLE_ANONYMOUS")))

        // Act
        val erSystemKontekst = SikkerhetContext.erSystemKontekst()
        val feil = catchThrowable { SikkerhetContext.hentSaksbehandler() }

        // Assert
        assertThat(erSystemKontekst).isFalse()
        assertThat(feil).isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `innlogget saksbehandler gir verdier fra token`() {
        // Arrange
        settSecurityHolderContext()

        // Act
        val saksbehandler = SikkerhetContext.hentSaksbehandler()
        val epost = SikkerhetContext.hentSaksbehandlerEpost()
        val navn = SikkerhetContext.hentSaksbehandlerNavn()
        val erSystemKontekst = SikkerhetContext.erSystemKontekst()

        // Assert
        assertThat(saksbehandler).isEqualTo(gyldigeClaims["NAVident"])
        assertThat(epost).isEqualTo(gyldigeClaims["preferred_username"])
        assertThat(navn).isEqualTo(gyldigeClaims["name"])
        assertThat(erSystemKontekst).isFalse()
    }

    @Test
    fun `harRolle gjelder bare rollene brukeren har`() {
        // Arrange
        settSecurityHolderContext()

        // Act
        val erSaksbehandler = SikkerhetContext.harRolle(Rolle.SAKSBEHANDLER)
        val erBeslutter = SikkerhetContext.harRolle(Rolle.BESLUTTER)

        // Assert
        assertThat(erSaksbehandler).isTrue()
        assertThat(erBeslutter).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = ["NAVident", "name", "preferred_username"])
    fun `innlogget person uten påkrevd claim får ikke systemverdi`(claim: String) {
        // Arrange
        settSecurityHolderContext(claims = gyldigeClaims - claim)

        // Act
        val erSystemKontekst = SikkerhetContext.erSystemKontekst()
        val feil =
            catchThrowable {
                SikkerhetContext.hentSaksbehandler()
                SikkerhetContext.hentSaksbehandlerNavn()
                SikkerhetContext.hentSaksbehandlerEpost()
            }

        // Assert
        assertThat(erSystemKontekst).isFalse()
        assertThat(feil).isInstanceOf(IllegalStateException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["NAVident", "name", "preferred_username"])
    fun `innlogget person med tomt påkrevd claim får ikke systemverdi`(claim: String) {
        // Arrange
        settSecurityHolderContext(claims = gyldigeClaims + (claim to " "))

        // Act
        val feil =
            catchThrowable {
                SikkerhetContext.hentSaksbehandler()
                SikkerhetContext.hentSaksbehandlerNavn()
                SikkerhetContext.hentSaksbehandlerEpost()
            }

        // Assert
        assertThat(feil).isInstanceOf(IllegalStateException::class.java)
    }
}
