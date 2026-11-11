package no.nav.gravferd.sak.inn.rest.behandling

import no.nav.gravferd.sak.kjerne.behandling.Behandling
import java.time.LocalDateTime

data class BehandlingDto(
    val id: Long,
    val fagsakId: Long,
    val versjon: Long,
    val opprettetAv: String,
    val opprettetTidspunkt: LocalDateTime,
    val endretAv: String?,
    val endretTidspunkt: LocalDateTime?,
)

fun Behandling.tilDto() =
    BehandlingDto(
        id = id,
        fagsakId = fagsakId,
        versjon = versjon,
        opprettetAv = opprettetAv,
        opprettetTidspunkt = opprettetTidspunkt,
        endretAv = endretAv,
        endretTidspunkt = endretTidspunkt,
    )
