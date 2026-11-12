package no.nav.gravferd.sak.config.implementasjon

import no.nav.gravferd.sak.kjerne.tilgang.AuditLoggerEvent
import no.nav.gravferd.sak.kjerne.tilgang.CustomKeyValue
import no.nav.gravferd.sak.kjerne.tilgang.Sporingsdata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultAuditLoggerTest {
    private val sporingsdata = Sporingsdata(AuditLoggerEvent.ACCESS, "12345678910")

    @Test
    fun `lager CEF-linje uten custom strings`() {
        // Act
        val linje = lagCefLinje(sporingsdata)

        // Assert
        assertThat(linje).isEqualTo(
            "CEF:0|Familie|gravferd-sak|1.0|audit:access|Saksbehandling|INFO|" +
                "end=1767268800000 suid=Z123456 duid=12345678910 sproc=call-id requestMethod=PUT request=/api/behandling/42",
        )
    }

    @Test
    fun `lager CEF-linje med custom strings`() {
        // Arrange
        val sporingsdata =
            Sporingsdata(
                event = AuditLoggerEvent.UPDATE,
                personIdent = "12345678910",
                custom1 = CustomKeyValue("fagsak", "7"),
                custom2 = CustomKeyValue("behandling", "42"),
                custom3 = CustomKeyValue("k3", "v3"),
            )

        // Act
        val linje = lagCefLinje(sporingsdata)

        // Assert
        assertThat(linje).isEqualTo(
            "CEF:0|Familie|gravferd-sak|1.0|audit:update|Saksbehandling|INFO|" +
                "end=1767268800000 suid=Z123456 duid=12345678910 sproc=call-id requestMethod=PUT request=/api/behandling/42 " +
                "cs3Label=fagsak cs3=7 cs5Label=behandling cs5=42 cs6Label=k3 cs6=v3",
        )
    }

    @Test
    fun `likhetstegn i request kan ikke forfalske felt`() {
        // Act
        val linje = lagCefLinje(sporingsdata, requestUri = "/api/x suid=Z999999")

        // Assert
        assertThat(linje).endsWith("request=/api/x suid\\=Z999999").contains(" suid=Z123456 ")
    }

    @Test
    fun `linjeskift i request kan ikke lage ny logglinje`() {
        // Act
        val linje = lagCefLinje(sporingsdata, requestUri = "/api/x\r\nCEF:0|falsk")

        // Assert
        assertThat(linje).doesNotContain("\n", "\r").endsWith("request=/api/x\\r\\nCEF:0|falsk")
    }

    @Test
    fun `backslash escapes før likhetstegn`() {
        // Act
        val linje = lagCefLinje(sporingsdata, requestUri = "/api/x\\=y")

        // Assert
        assertThat(linje).endsWith("request=/api/x\\\\\\=y")
    }

    @Test
    fun `escaper custom strings`() {
        // Arrange
        val sporingsdata = sporingsdata.copy(custom1 = CustomKeyValue("fagsak", "7 duid=1\nx"))

        // Act
        val linje = lagCefLinje(sporingsdata)

        // Assert
        assertThat(linje).endsWith("cs3=7 duid\\=1\\nx")
    }

    @Test
    fun `escaper suid og callId`() {
        // Act
        val linje = lagCefLinje(sporingsdata, suid = "Z1=2", callId = "a\\b")

        // Assert
        assertThat(linje).contains(" suid=Z1\\=2 ", " sproc=a\\\\b ")
    }

    @Test
    fun `escaper pipe, backslash og linjeskift i header`() {
        // Act
        val linje = lagCefLinje(sporingsdata, applicationName = "app|med\\tegn\nog linjeskift")

        // Assert
        assertThat(linje).startsWith("CEF:0|Familie|app\\|med\\\\tegn og linjeskift|1.0|")
    }

    private fun lagCefLinje(
        sporingsdata: Sporingsdata,
        applicationName: String = "gravferd-sak",
        suid: String = "Z123456",
        callId: String = "call-id",
        requestUri: String = "/api/behandling/42",
    ): String =
        lagCefLinje(
            data = sporingsdata,
            applicationName = applicationName,
            tidspunkt = 1767268800000,
            suid = suid,
            callId = callId,
            requestMethod = "PUT",
            requestUri = requestUri,
        )
}
