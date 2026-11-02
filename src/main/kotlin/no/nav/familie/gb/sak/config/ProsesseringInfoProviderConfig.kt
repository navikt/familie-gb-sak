package no.nav.familie.gb.sak.config

// import no.nav.familie.prosessering.config.ProsesseringInfoProvider
import no.nav.familie.sikkerhet.context.TokenContextHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProsesseringInfoProviderConfig(
    @param:Value("\${prosessering.rolle}") private val prosesseringRolle: String,
) {
//    @Bean
//    fun prosesseringInfoProvider() =
//        object : ProsesseringInfoProvider {
//            override fun hentBrukernavn(): String = TokenContextHolder.getClaimAsString("preferred_username") ?: SYSTEM_FORKORTELSE
//
//            override fun harTilgang(): Boolean = TokenContextHolder.getClaimAsStringList("groups")?.contains(prosesseringRolle) == true
//        }

    companion object {
        private const val SYSTEM_FORKORTELSE = "VL"
    }
}
