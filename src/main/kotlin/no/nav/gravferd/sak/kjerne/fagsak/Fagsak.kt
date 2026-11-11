package no.nav.gravferd.sak.kjerne.fagsak

import java.time.LocalDateTime

data class Fagsak(
    val id: Long,
    val versjon: Long,
    val opprettetAv: String,
    val opprettetTidspunkt: LocalDateTime,
    val endretAv: String?,
    val endretTidspunkt: LocalDateTime?,
)
