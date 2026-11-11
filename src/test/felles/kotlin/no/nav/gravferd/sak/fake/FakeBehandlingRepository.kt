package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.kjerne.behandling.Behandling
import no.nav.gravferd.sak.kjerne.behandling.BehandlingRepository
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class FakeBehandlingRepository : BehandlingRepository {
    private val nesteId = AtomicLong(1)
    private val behandlinger = ConcurrentHashMap<Long, Behandling>()

    override fun opprett(fagsakId: Long): Behandling {
        val behandling =
            Behandling(
                id = nesteId.getAndIncrement(),
                fagsakId = fagsakId,
                versjon = 1,
                opprettetAv = "VL",
                opprettetTidspunkt = LocalDateTime.of(2026, 1, 1, 12, 0),
                endretAv = "VL",
                endretTidspunkt = LocalDateTime.of(2026, 1, 1, 12, 0),
            )
        behandlinger[behandling.id] = behandling
        return behandling
    }

    override fun hent(id: Long): Behandling? = behandlinger[id]

    override fun hentForFagsak(fagsakId: Long): List<Behandling> = behandlinger.values.filter { it.fagsakId == fagsakId }
}
