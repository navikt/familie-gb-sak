package no.nav.gravferd.sak.common

import no.nav.gravferd.sak.config.Profil
import no.nav.gravferd.sak.config.erAktiv
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class EnvService(
    private val environment: Environment,
) {
    fun erProd() = environment.erAktiv(Profil.Prod)

    fun erPreprod() = environment.erAktiv(Profil.Preprod)

    fun erDev() = environment.erAktiv(Profil.DevPostgresPreprod)
}
