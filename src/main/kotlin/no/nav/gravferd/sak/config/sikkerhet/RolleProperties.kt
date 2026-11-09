package no.nav.gravferd.sak.config.sikkerhet

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("rolle")
class RolleProperties(
    val beslutter: String,
    val saksbehandler: String,
    val veileder: String,
    val forvalter: String,
    val kode6: String,
)
