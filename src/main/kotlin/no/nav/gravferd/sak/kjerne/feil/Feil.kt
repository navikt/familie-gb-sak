package no.nav.gravferd.sak.kjerne.feil

sealed class BaseFeil(
    val melding: String,
    val frontendFeilmelding: String,
    cause: Throwable? = null,
) : RuntimeException(melding, cause)

open class Feil(
    melding: String,
    frontendFeilmelding: String = "Noe gikk galt. Prøv igjen senere.",
    cause: Throwable? = null,
) : BaseFeil(melding, frontendFeilmelding, cause)

open class FunksjonellFeil(
    melding: String,
    frontendFeilmelding: String = melding,
    cause: Throwable? = null,
) : BaseFeil(melding, frontendFeilmelding, cause)

class IkkeFunnetFeil(
    melding: String,
    frontendFeilmelding: String = melding,
) : FunksjonellFeil(melding, frontendFeilmelding)
