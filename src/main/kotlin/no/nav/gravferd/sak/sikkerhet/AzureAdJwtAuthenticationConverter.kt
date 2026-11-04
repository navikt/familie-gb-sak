package no.nav.gravferd.sak.sikkerhet

import no.nav.gravferd.sak.common.secureLogger
import no.nav.gravferd.sak.config.RolleConfig
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class AzureAdJwtAuthenticationConverter(
    private val rolleConfig: RolleConfig,
) : Converter<Jwt, JwtAuthenticationToken> {
    override fun convert(jwt: Jwt): JwtAuthenticationToken {
        val grupper = jwt.getClaimAsStringList("groups") ?: emptyList()

        val roller =
            buildSet {
                if (grupper.contains(rolleConfig.veilederRolle)) add(Rolle.VEILEDER)
                if (grupper.contains(rolleConfig.forvalterRolle)) add(Rolle.FORVALTER)
                if (grupper.contains(rolleConfig.saksbehandlerRolle)) add(Rolle.SAKSBEHANDLER)
                if (grupper.contains(rolleConfig.beslutterRolle)) add(Rolle.BESLUTTER)
            }

        if (roller.isEmpty()) {
            secureLogger.warn("Bruker har ingen gyldige roller. Grupper i token: ${grupper.joinToString(", ")}")
        }

        return JwtAuthenticationToken(jwt, roller.map { SimpleGrantedAuthority(it.authority()) })
    }
}
