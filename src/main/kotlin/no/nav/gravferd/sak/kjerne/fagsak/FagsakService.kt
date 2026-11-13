package no.nav.gravferd.sak.kjerne.fagsak

import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetFeil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FagsakService(
    private val fagsakRepository: FagsakRepository,
) {
    @Transactional
    fun opprett(): Fagsak = fagsakRepository.opprett()

    @Transactional(readOnly = true)
    fun hent(id: Long): Fagsak = fagsakRepository.hent(id) ?: throw IkkeFunnetFeil("Fant ikke fagsak $id")
}
