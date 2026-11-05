package no.nav.gravferd.sak.sikkerhet

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.SignedJWT
import no.nav.gravferd.sak.sikkerhet.TestRolleConfig.rolleConfig
import no.nav.gravferd.sak.sikkerhet.TestTokenServer.DEFAULT_AUDIENCE
import no.nav.gravferd.sak.sikkerhet.TestTokenServer.DEFAULT_SUBJECT
import no.nav.gravferd.sak.sikkerhet.TestTokenServer.token
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.UUID

@Tag("integration")
class AzureAdAuthenticationManagerTest {
    private val authenticationManager =
        AzureAdAuthenticationManager(
            jwksUri = TestTokenServer.jwksUri,
            issuer = TestTokenServer.issuer,
            audience = DEFAULT_AUDIENCE,
            jwtAuthenticationConverter = AzureAdJwtAuthenticationConverter(rolleConfig),
        )

    @Test
    fun `godtar gyldig token og mapper grupper til roller`() {
        // Arrange
        val token = token(grupper = listOf(rolleConfig.saksbehandler))

        // Act
        val authentication = authenticationManager.authenticate(BearerTokenAuthenticationToken(token))

        // Assert
        assertThat(authentication).isInstanceOf(JwtAuthenticationToken::class.java)
        assertThat(authentication.isAuthenticated).isTrue()
        assertThat(authentication.name).isEqualTo(DEFAULT_SUBJECT)
        assertThat(authentication.authorities.map { it.authority }).containsExactly("ROLE_SAKSBEHANDLER")
    }

    @Test
    fun `godtar gyldig token uten roller`() {
        // Arrange
        val token = token()

        // Act
        val authentication = authenticationManager.authenticate(BearerTokenAuthenticationToken(token))

        // Assert
        assertThat(authentication.isAuthenticated).isTrue()
        assertThat(authentication.authorities).isEmpty()
    }

    @Test
    fun `avviser token med feil issuer`() {
        // Arrange
        val token = token(ekstraClaims = mapOf("iss" to "https://annen-issuer.example"))

        // Act
        val feil = catchThrowable { authenticationManager.authenticate(BearerTokenAuthenticationToken(token)) }

        // Assert
        assertThat(feil).isInstanceOf(AuthenticationException::class.java)
    }

    @Test
    fun `avviser token med feil audience`() {
        // Arrange
        val token = token(audience = "annen-app")

        // Act
        val feil = catchThrowable { authenticationManager.authenticate(BearerTokenAuthenticationToken(token)) }

        // Assert
        assertThat(feil).isInstanceOf(AuthenticationException::class.java)
    }

    @Test
    fun `avviser utløpt token`() {
        // Arrange
        val token = token(expiry = -3600)

        // Act
        val feil = catchThrowable { authenticationManager.authenticate(BearerTokenAuthenticationToken(token)) }

        // Assert
        assertThat(feil).isInstanceOf(AuthenticationException::class.java)
    }

    @Test
    fun `avviser token signert med ukjent nøkkel`() {
        // Arrange
        val claims = SignedJWT.parse(token()).jwtClaimsSet
        val ukjentNøkkel = RSAKeyGenerator(2048).keyID(UUID.randomUUID().toString()).generate()
        val header = JWSHeader.Builder(JWSAlgorithm.RS256).keyID(ukjentNøkkel.keyID).build()
        val token = SignedJWT(header, claims).apply { sign(RSASSASigner(ukjentNøkkel)) }.serialize()

        // Act
        val feil = catchThrowable { authenticationManager.authenticate(BearerTokenAuthenticationToken(token)) }

        // Assert
        assertThat(feil).isInstanceOf(AuthenticationException::class.java)
    }

    @Test
    fun `avviser token med manipulert payload`() {
        // Arrange
        val gyldig = token().split(".")
        val annen = token(grupper = listOf(rolleConfig.beslutter)).split(".")
        val token = listOf(gyldig[0], annen[1], gyldig[2]).joinToString(".")

        // Act
        val feil = catchThrowable { authenticationManager.authenticate(BearerTokenAuthenticationToken(token)) }

        // Assert
        assertThat(feil).isInstanceOf(AuthenticationException::class.java)
    }

    @Test
    fun `avviser streng som ikke er jwt`() {
        // Arrange
        val token = "ikke-en-jwt"

        // Act
        val feil = catchThrowable { authenticationManager.authenticate(BearerTokenAuthenticationToken(token)) }

        // Assert
        assertThat(feil).isInstanceOf(AuthenticationException::class.java)
    }
}
