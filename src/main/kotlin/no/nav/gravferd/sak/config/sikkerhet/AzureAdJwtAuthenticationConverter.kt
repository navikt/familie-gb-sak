package no.nav.gravferd.sak.config.sikkerhet

import no.nav.gravferd.sak.common.secureLogger
import no.nav.gravferd.sak.kjerne.tilgang.Rolle
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class AzureAdJwtAuthenticationConverter(
    private val rolleProperties: RolleProperties,
) : Converter<Jwt, JwtAuthenticationToken> {
    override fun convert(jwt: Jwt): JwtAuthenticationToken {
        val grupper = jwt.getClaimAsStringList("groups") ?: emptyList()

        val roller =
            buildSet {
                if (grupper.contains(rolleProperties.veileder)) add(Rolle.VEILEDER)
                if (grupper.contains(rolleProperties.forvalter)) add(Rolle.FORVALTER)
                if (grupper.contains(rolleProperties.saksbehandler)) add(Rolle.SAKSBEHANDLER)
                if (grupper.contains(rolleProperties.beslutter)) add(Rolle.BESLUTTER)
            }

        if (roller.isEmpty()) {
            secureLogger.warn("Bruker har ingen gyldige roller. Grupper i token: ${grupper.joinToString(", ")}")
        }

        return JwtAuthenticationToken(jwt, roller.map { SimpleGrantedAuthority(it.authority()) })
    }
}

fun Rolle.authority(): String = "ROLE_$name"

fun rollerMedInternTilgang(): Array<String> =
    setOf(Rolle.VEILEDER, Rolle.FORVALTER, Rolle.SAKSBEHANDLER, Rolle.BESLUTTER).map { it.name }.toTypedArray()
