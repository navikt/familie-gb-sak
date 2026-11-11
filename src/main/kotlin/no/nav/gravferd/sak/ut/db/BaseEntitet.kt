package no.nav.gravferd.sak.ut.db

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

abstract class BaseEntitet {
    @CreatedBy
    @Column("opprettet_av")
    var opprettetAv: String? = null

    @CreatedDate
    @Column("opprettet_tidspunkt")
    var opprettetTidspunkt: LocalDateTime? = null

    @LastModifiedBy
    @Column("endret_av")
    var endretAv: String? = null

    @LastModifiedDate
    @Column("endret_tidspunkt")
    var endretTidspunkt: LocalDateTime? = null

    @Version
    @Column("versjon")
    var versjon: Long = 0
}
