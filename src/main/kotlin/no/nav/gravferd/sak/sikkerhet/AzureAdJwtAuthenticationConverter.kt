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
                if (grupper.contains(rolleConfig.VEILEDER_ROLLE)) add(Rolle.VEILEDER)
                if (grupper.contains(rolleConfig.FORVALTER_ROLLE)) add(Rolle.FORVALTER)
                if (grupper.contains(rolleConfig.SAKSBEHANDLER_ROLLE)) add(Rolle.SAKSBEHANDLER)
                if (grupper.contains(rolleConfig.BESLUTTER_ROLLE)) add(Rolle.BESLUTTER)
            }

        if (roller.isEmpty()) {
            secureLogger.warn("Bruker har ingen gyldige roller. Grupper i token: ${grupper.joinToString(", ")}")
        }

        return JwtAuthenticationToken(jwt, roller.map { SimpleGrantedAuthority(it.authority()) })
    }
}
