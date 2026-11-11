package no.nav.gravferd.sak.config.sikkerhet

import no.nav.gravferd.sak.kjerne.tilgang.Rolle
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

val gyldigeClaims: Map<String, Any> =
    mapOf(
        "NAVident" to "Z123456",
        "name" to "Ola Nordmann",
        "preferred_username" to "ola.nordmann@nav.no",
    )

fun lagJwt(
    claims: Map<String, Any> = gyldigeClaims,
    grupper: List<String>? = null,
): Jwt =
    Jwt
        .withTokenValue("test-token")
        .header("alg", "RS256")
        .subject("test-bruker")
        .claims {
            it.putAll(claims)
            grupper?.let { grupper -> it["groups"] = grupper }
        }.build()

fun settSecurityHolderContext(
    claims: Map<String, Any> = gyldigeClaims,
    roller: List<Rolle> = listOf(Rolle.SAKSBEHANDLER),
) {
    SecurityContextHolder.getContext().authentication =
        JwtAuthenticationToken(lagJwt(claims), roller.map { SimpleGrantedAuthority(it.authority()) })
}
