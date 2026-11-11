package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.kjerne.fagsak.FagsakRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.findById
import org.springframework.stereotype.Repository

@Repository
class DefaultFagsakRepository(
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
) : FagsakRepository {
    override fun opprett(): Fagsak = jdbcAggregateTemplate.insert(FagsakDbo()).tilDomene()

    override fun hent(id: Long): Fagsak? = jdbcAggregateTemplate.findById<FagsakDbo>(id)?.tilDomene()

    private fun FagsakDbo.tilDomene() =
        Fagsak(
            id = checkNotNull(id) { "Fagsak mangler id etter lagring" },
            versjon = versjon,
            opprettetAv = checkNotNull(opprettetAv) { "Fagsak mangler opprettetAv" },
            opprettetTidspunkt = checkNotNull(opprettetTidspunkt) { "Fagsak mangler opprettetTidspunkt" },
            endretAv = endretAv,
            endretTidspunkt = endretTidspunkt,
        )
}
