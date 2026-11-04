package no.nav.gravferd.sak.sikkerhet

enum class Rolle {
    VEILEDER,
    FORVALTER,
    SAKSBEHANDLER,
    BESLUTTER,
    ;

    fun authority(): String = "ROLE_$name"

    companion object {
        fun rollerMedInternTilgang() = setOf(VEILEDER.name, FORVALTER.name, SAKSBEHANDLER.name, BESLUTTER.name).toTypedArray()
    }
}
