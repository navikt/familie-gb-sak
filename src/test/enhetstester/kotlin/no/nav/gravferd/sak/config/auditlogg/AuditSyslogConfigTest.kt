package no.nav.gravferd.sak.config.auditlogg

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslog
import org.slf4j.LoggerFactory
import java.net.ServerSocket

class AuditSyslogConfigTest {
    private val personident = "01010199999"
    private val saksbehandler = "Z999999"

    @Test
    fun `bruker bare egen backlog-handler`() {
        // Act
        val handlere = AuditSyslogConfig().backLogHandlers

        // Assert
        assertThat(handlere).hasSize(1).allMatch { it is AuditBackLogHandler }
    }

    @Test
    fun `varsler om leveringsfeil uten auditinnhold`() {
        // Arrange
        val ledigPort = ServerSocket(0).use { it.localPort }
        val syslog = lagSyslog(port = ledigPort)
        val logger = hentLogbackLogger()
        val appender = ListAppender<ILoggingEvent>().apply { start() }
        logger.addAppender(appender)

        // Act
        try {
            syslog.info("CEF:0|Familie|gravferd-sak|1.0|audit:access|Saksbehandling|INFO|suid=$saksbehandler duid=$personident")
        } finally {
            logger.detachAppender(appender)
            syslog.shutdown()
        }

        // Assert
        val meldinger = synchronized(appender) { appender.list.map { it.formattedMessage } }
        assertThat(meldinger).contains("Klarte ikke levere auditlogg")
        assertThat(meldinger).noneMatch { it.contains(personident) || it.contains(saksbehandler) || it.contains("CEF:0") }
    }

    private fun lagSyslog(port: Int): TCPNetSyslog {
        val config =
            AuditSyslogConfig().apply {
                host = "127.0.0.1"
                this.port = port
                isThreaded = false
                writeRetries = 0
            }
        return TCPNetSyslog().apply { initialize("tcp", config) }
    }

    private fun hentLogbackLogger(): Logger {
        repeat(100) {
            val loggerFactory = LoggerFactory.getILoggerFactory()
            if (loggerFactory is LoggerContext) return loggerFactory.getLogger(AuditBackLogHandler::class.java)
            Thread.sleep(10)
        }
        error("Logback ble ikke initialisert")
    }
}
