package no.nav.gravferd.sak.sikkerhet

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtAudienceValidator
import org.springframework.security.oauth2.jwt.JwtIssuerValidator
import org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.stereotype.Component

@Component
class AzureAdAuthenticationManager(
    @Value("\${AZURE_OPENID_CONFIG_JWKS_URI}") jwksUri: String,
    @Value("\${AZURE_OPENID_CONFIG_ISSUER}") issuer: String,
    @Value("\${AZURE_APP_CLIENT_ID}") audience: String,
    jwtAuthenticationConverter: AzureAdJwtAuthenticationConverter,
) : AuthenticationManager {
    private val authenticationManager: AuthenticationManager =
        run {
            val decoder =
                NimbusJwtDecoder.withJwkSetUri(jwksUri).build().also {
                    it.setJwtValidator(
                        createDefaultWithValidators(
                            JwtIssuerValidator(issuer),
                            JwtAudienceValidator(audience),
                        ),
                    )
                }
            val provider = JwtAuthenticationProvider(decoder)
            provider.setJwtAuthenticationConverter(jwtAuthenticationConverter)
            ProviderManager(provider)
        }

    override fun authenticate(authentication: Authentication): Authentication = authenticationManager.authenticate(authentication)
}
