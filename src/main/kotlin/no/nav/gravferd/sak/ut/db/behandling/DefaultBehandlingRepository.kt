package no.nav.gravferd.sak.ut.db.behandling

import no.nav.gravferd.sak.kjerne.behandling.Behandling
import no.nav.gravferd.sak.kjerne.behandling.BehandlingRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.findAll
import org.springframework.data.jdbc.core.findById
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.stereotype.Repository

@Repository
class DefaultBehandlingRepository(
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
) : BehandlingRepository {
    override fun opprett(fagsakId: Long): Behandling = jdbcAggregateTemplate.insert(BehandlingDbo(fagsakId = fagsakId)).tilDomene()

    override fun hent(id: Long): Behandling? = jdbcAggregateTemplate.findById<BehandlingDbo>(id)?.tilDomene()

    override fun hentForFagsak(fagsakId: Long): List<Behandling> =
        jdbcAggregateTemplate
            .findAll<BehandlingDbo>(Query.query(Criteria.where("fagsakId") isEqual fagsakId))
            .map { it.tilDomene() }

    private fun BehandlingDbo.tilDomene() =
        Behandling(
            id = checkNotNull(id) { "Behandling mangler id etter lagring" },
            fagsakId = fagsakId,
            versjon = versjon,
            opprettetAv = checkNotNull(opprettetAv) { "Behandling mangler opprettetAv" },
            opprettetTidspunkt = checkNotNull(opprettetTidspunkt) { "Behandling mangler opprettetTidspunkt" },
            endretAv = endretAv,
            endretTidspunkt = endretTidspunkt,
        )
}
