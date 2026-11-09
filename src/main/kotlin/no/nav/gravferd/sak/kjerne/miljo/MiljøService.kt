package no.nav.gravferd.sak.kjerne.miljo

interface MiljøService {
    fun erProd(): Boolean

    fun erPreprod(): Boolean

    fun erDev(): Boolean
}
