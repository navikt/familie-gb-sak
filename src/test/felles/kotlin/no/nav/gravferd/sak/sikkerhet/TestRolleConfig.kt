package no.nav.gravferd.sak.sikkerhet

import no.nav.gravferd.sak.config.RolleConfig

object TestRolleConfig {
    val rolleConfig =
        RolleConfig(
            beslutterRolle = "beslutter-gruppe",
            saksbehandlerRolle = "saksbehandler-gruppe",
            veilederRolle = "veileder-gruppe",
            forvalterRolle = "forvalter-gruppe",
            kode6 = "kode6-gruppe",
        )
}
