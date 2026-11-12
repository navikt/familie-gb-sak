package no.nav.gravferd.sak.kjerne.tilgang

interface AuditLogger {
    fun log(data: Sporingsdata)
}

enum class AuditLoggerEvent(
    val type: String,
) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    ACCESS("access"),
}

data class Sporingsdata(
    val event: AuditLoggerEvent,
    val personIdent: String,
    val custom1: CustomKeyValue? = null,
    val custom2: CustomKeyValue? = null,
    val custom3: CustomKeyValue? = null,
)

data class CustomKeyValue(
    val key: String,
    val value: String,
)
