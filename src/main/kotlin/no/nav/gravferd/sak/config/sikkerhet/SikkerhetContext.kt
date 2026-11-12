package no.nav.gravferd.sak.config.sikkerhet

import no.nav.gravferd.sak.kjerne.tilgang.Rolle
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

object SikkerhetContext {
    const val SYSTEM_FORKORTELSE = "VL"
    const val SYSTEM_NAVN = "System"

    fun erSystemKontekst(): Boolean = SecurityContextHolder.getContext().authentication == null

    fun erMaskinTilMaskinToken(): Boolean = hentJwt()?.getClaimAsString("idtyp") == "app"

    fun hentSaksbehandler(): String = hentClaimEllerSystemverdi("NAVident", SYSTEM_FORKORTELSE)

    fun hentSaksbehandlerEpost(): String = hentClaimEllerSystemverdi("preferred_username", SYSTEM_FORKORTELSE)

    fun hentSaksbehandlerNavn(): String = hentClaimEllerSystemverdi("name", SYSTEM_NAVN)

    fun harRolle(rolle: Rolle): Boolean = hentAutentisering()?.authorities?.any { it.authority == rolle.authority() } == true

    fun hentJwt(): Jwt? = hentAutentisering()?.token

    private fun hentClaimEllerSystemverdi(
        claim: String,
        systemverdi: String,
    ): String {
        val jwt = hentJwt() ?: return systemverdi
        return jwt.getClaimAsString(claim)?.takeIf { it.isNotBlank() } ?: error("Innlogget bruker mangler $claim i token")
    }

    private fun hentAutentisering(): JwtAuthenticationToken? =
        when (val autentisering = SecurityContextHolder.getContext().authentication) {
            null -> null
            is JwtAuthenticationToken -> autentisering
            else -> error("Innlogget bruker er ikke autentisert med JWT")
        }
}
