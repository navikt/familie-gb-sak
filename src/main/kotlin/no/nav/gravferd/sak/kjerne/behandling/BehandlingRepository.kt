package no.nav.gravferd.sak.kjerne.behandling

interface BehandlingRepository {
    fun opprett(fagsakId: Long): Behandling

    fun hent(id: Long): Behandling?

    fun hentForFagsak(fagsakId: Long): List<Behandling>
}
