package no.nav.gravferd.sak.sikkerhet

import no.nav.gravferd.sak.config.RolleConfig

object TestRolleConfig {
    val rolleConfig =
        RolleConfig(
            beslutter = "beslutter-gruppe",
            saksbehandler = "saksbehandler-gruppe",
            veileder = "veileder-gruppe",
            forvalter = "forvalter-gruppe",
            kode6 = "kode6-gruppe",
        )
}
