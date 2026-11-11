package no.nav.gravferd.sak.ut.db.behandling

import no.nav.gravferd.sak.ut.db.BaseEntitet
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("behandling")
class BehandlingDbo(
    @Id
    var id: Long? = null,
    @Column("fk_fagsak_id")
    val fagsakId: Long,
) : BaseEntitet()
