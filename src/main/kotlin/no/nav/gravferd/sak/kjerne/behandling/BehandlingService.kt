package no.nav.gravferd.sak.kjerne.behandling

import no.nav.gravferd.sak.kjerne.fagsak.FagsakService
import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BehandlingService(
    private val behandlingRepository: BehandlingRepository,
    private val fagsakService: FagsakService,
) {
    @Transactional
    fun opprett(fagsakId: Long): Behandling {
        val fagsak = fagsakService.hent(fagsakId)
        return behandlingRepository.opprett(fagsak.id)
    }

    @Transactional(readOnly = true)
    fun hent(id: Long): Behandling = behandlingRepository.hent(id) ?: throw IkkeFunnetException("Fant ikke behandling $id")

    @Transactional(readOnly = true)
    fun hentForFagsak(fagsakId: Long): List<Behandling> {
        val fagsak = fagsakService.hent(fagsakId)
        return behandlingRepository.hentForFagsak(fagsak.id)
    }
}
