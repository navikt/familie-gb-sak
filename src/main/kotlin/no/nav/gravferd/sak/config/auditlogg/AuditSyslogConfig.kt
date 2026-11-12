package no.nav.gravferd.sak.config.auditlogg

import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig

class AuditSyslogConfig : TCPNetSyslogConfig() {
    init {
        addBackLogHandler(AuditBackLogHandler())
    }
}
