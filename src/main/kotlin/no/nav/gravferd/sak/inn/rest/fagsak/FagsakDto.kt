package no.nav.gravferd.sak.inn.rest.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import java.time.LocalDateTime

data class FagsakDto(
    val id: Long,
    val versjon: Long,
    val opprettetAv: String,
    val opprettetTidspunkt: LocalDateTime,
    val endretAv: String?,
    val endretTidspunkt: LocalDateTime?,
)

fun Fagsak.tilDto() =
    FagsakDto(
        id = id,
        versjon = versjon,
        opprettetAv = opprettetAv,
        opprettetTidspunkt = opprettetTidspunkt,
        endretAv = endretAv,
        endretTidspunkt = endretTidspunkt,
    )
