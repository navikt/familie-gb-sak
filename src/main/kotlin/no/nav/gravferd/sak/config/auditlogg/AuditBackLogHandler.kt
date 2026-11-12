package no.nav.gravferd.sak.config.auditlogg

import org.productivity.java.syslog4j.SyslogBackLogHandlerIF
import org.productivity.java.syslog4j.SyslogIF
import org.slf4j.LoggerFactory

/**
 * Erstatter syslog4js standardhandler, som skriver hele auditmeldingen (med `suid` og `duid`)
 * til `System.err` når leveringen feiler. Det går utenom Logback, så `additivity="false"` hjelper ikke.
 * En tom handlerliste er ikke nok, siden syslog4j da faller tilbake til standardhandleren.
 */
class AuditBackLogHandler : SyslogBackLogHandlerIF {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun initialize() = Unit

    override fun down(
        syslog: SyslogIF?,
        reason: String?,
    ) {
        logger.error("Mistet forbindelsen til auditmottakeren")
    }

    override fun up(syslog: SyslogIF?) {
        logger.info("Forbindelsen til auditmottakeren er gjenopprettet")
    }

    override fun log(
        syslog: SyslogIF?,
        level: Int,
        message: String?,
        reason: String?,
    ) {
        logger.error("Klarte ikke levere auditlogg")
    }
}
