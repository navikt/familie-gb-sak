package no.nav.gravferd.sak.sikkerhet

import no.nav.gravferd.sak.config.RolleConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.security.oauth2.jwt.Jwt

class AzureAdJwtAuthenticationConverterTest {
    private val rolleConfig =
        RolleConfig(
            beslutterRolle = "beslutter-gruppe",
            saksbehandlerRolle = "saksbehandler-gruppe",
            veilederRolle = "veileder-gruppe",
            forvalterRolle = "forvalter-gruppe",
            kode6 = "kode6-gruppe",
            kode7 = "kode7-gruppe",
        )
    private val converter = AzureAdJwtAuthenticationConverter(rolleConfig)

    @ParameterizedTest
    @CsvSource(
        "veileder-gruppe, ROLE_VEILEDER",
        "forvalter-gruppe, ROLE_FORVALTER",
        "saksbehandler-gruppe, ROLE_SAKSBEHANDLER",
        "beslutter-gruppe, ROLE_BESLUTTER",
    )
    fun `mapper hver kjent gruppe til kun tilhorende rolle`(
        gruppe: String,
        forventetAuthority: String,
    ) {
        // Arrange
        val jwt = jwtMedGrupper(listOf(gruppe))

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities.map { it.authority }).containsExactly(forventetAuthority)
    }

    @Test
    fun `mapper flere kjente grupper til alle tilhorende roller`() {
        // Arrange
        val jwt =
            jwtMedGrupper(
                listOf(
                    rolleConfig.veilederRolle,
                    rolleConfig.forvalterRolle,
                    rolleConfig.saksbehandlerRolle,
                    rolleConfig.beslutterRolle,
                ),
            )

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities.map { it.authority }).containsExactlyInAnyOrder(
            "ROLE_VEILEDER",
            "ROLE_FORVALTER",
            "ROLE_SAKSBEHANDLER",
            "ROLE_BESLUTTER",
        )
        assertThat(authentication.token).isSameAs(jwt)
    }

    @Test
    fun `ignorerer ukjente grupper sammen med kjente grupper`() {
        // Arrange
        val jwt = jwtMedGrupper(listOf("ukjent-gruppe", rolleConfig.saksbehandlerRolle))

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities.map { it.authority }).containsExactly("ROLE_SAKSBEHANDLER")
    }

    @Test
    fun `gir ikke dupliserte roller ved gjentatte grupper`() {
        // Arrange
        val jwt = jwtMedGrupper(listOf(rolleConfig.veilederRolle, rolleConfig.veilederRolle))

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities.map { it.authority }).containsExactly("ROLE_VEILEDER")
    }

    @Test
    fun `gir ingen roller uten groups claim`() {
        // Arrange
        val jwt =
            Jwt
                .withTokenValue("test-token")
                .header("alg", "RS256")
                .subject("test-bruker")
                .build()

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities).isEmpty()
    }

    @Test
    fun `gir ingen roller med tom gruppeliste`() {
        // Arrange
        val jwt = jwtMedGrupper(emptyList())

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities).isEmpty()
    }

    @Test
    fun `gir ingen roller med bare ukjente grupper`() {
        // Arrange
        val jwt = jwtMedGrupper(listOf("ukjent-gruppe"))

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities).isEmpty()
    }

    @Test
    fun `kode6 og kode7 gir ikke behandlerroller`() {
        // Arrange
        val jwt = jwtMedGrupper(listOf(rolleConfig.kode6, rolleConfig.kode7))

        // Act
        val authentication = converter.convert(jwt)

        // Assert
        assertThat(authentication.authorities).isEmpty()
    }

    private fun jwtMedGrupper(grupper: List<String>): Jwt =
        Jwt
            .withTokenValue("test-token")
            .header("alg", "RS256")
            .subject("test-bruker")
            .claim("groups", grupper)
            .build()
}
