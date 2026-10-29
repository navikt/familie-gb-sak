package no.nav.familie.gb.sak.config

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.resilience.annotation.EnableResilientMethods

@SpringBootConfiguration
@ComponentScan(
    "no.nav.familie.prosessering",
    "no.nav.familie.unleash",
    ApplicationConfig.PAKKENAVN,
    "no.nav.familie.metrikker",
    "no.nav.familie.felles.tokenklient.entraid",
)
@EnableResilientMethods
@ConfigurationPropertiesScan
@Import(
    no.nav.familie.sikkerhet.context.FamilieFellesSpringSecurityKonfigurasjon::class,
    no.nav.familie.tilgangsmaskin.TilgangsmaskinKlientConfig::class,
)
class ApplicationConfig {
    companion object {
        const val PAKKENAVN = "no.nav.familie.gb.sak"
    }
}
