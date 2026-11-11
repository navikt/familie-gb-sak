package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.kjerne.fagsak.FagsakRepository
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class FakeFagsakRepository : FagsakRepository {
    private val nesteId = AtomicLong(1)
    private val fagsaker = ConcurrentHashMap<Long, Fagsak>()

    override fun opprett(): Fagsak {
        val fagsak =
            Fagsak(
                id = nesteId.getAndIncrement(),
                versjon = 1,
                opprettetAv = "VL",
                opprettetTidspunkt = LocalDateTime.of(2026, 1, 1, 12, 0),
                endretAv = "VL",
                endretTidspunkt = LocalDateTime.of(2026, 1, 1, 12, 0),
            )
        fagsaker[fagsak.id] = fagsak
        return fagsak
    }

    override fun hent(id: Long): Fagsak? = fagsaker[id]
}
