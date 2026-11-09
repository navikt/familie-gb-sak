package no.nav.gravferd.sak.config.implementasjon

import no.nav.gravferd.sak.config.Profil
import no.nav.gravferd.sak.config.erAktiv
import no.nav.gravferd.sak.kjerne.miljo.MiljøService
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class DefaultMiljøService(
    private val environment: Environment,
) : MiljøService {
    override fun erProd() = environment.erAktiv(Profil.Prod)

    override fun erPreprod() = environment.erAktiv(Profil.Preprod)

    override fun erDev() = environment.erAktiv(Profil.DevPostgresPreprod)
}
