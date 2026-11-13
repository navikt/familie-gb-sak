package no.nav.gravferd.sak.config.auditlogg

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuditSyslogConfigTest {
    @Test
    fun `bruker bare egen backlog-handler`() {
        // Act
        val handlere = AuditSyslogConfig().backLogHandlers

        // Assert
        assertThat(handlere).hasSize(1).allMatch { it is AuditBackLogHandler }
    }
}
