package no.nav.gravferd.sak.kjerne.behandling

import java.time.LocalDateTime

data class Behandling(
    val id: Long,
    val fagsakId: Long,
    val versjon: Long,
    val opprettetAv: String,
    val opprettetTidspunkt: LocalDateTime,
    val endretAv: String?,
    val endretTidspunkt: LocalDateTime?,
)
