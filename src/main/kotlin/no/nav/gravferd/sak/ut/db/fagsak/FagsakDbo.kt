package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.ut.db.BaseEntitet
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("fagsak")
class FagsakDbo(
    @Id
    var id: Long? = null,
) : BaseEntitet()
