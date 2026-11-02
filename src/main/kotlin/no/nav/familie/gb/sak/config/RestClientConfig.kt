package no.nav.familie.gb.sak.config

import no.nav.familie.felles.tokenklient.entraid.EntraIDRestClientFactory
import no.nav.familie.sikkerhet.context.TokenContextHolder
import no.nav.familie.tilgangsmaskin.TilgangsmaskinKlientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig(
    private val entraIDRestClientFactory: EntraIDRestClientFactory,
) {
    @Bean(TilgangsmaskinKlientConfig.TILGANGSMASKIN_OBO_REST_CLIENT)
    fun tilgangsmaskinOboRestClient(
        @Value("\${TILGANGSMASKIN_SCOPE}") scope: String,
    ): RestClient {
        val requestFactory =
            SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(Duration.ofSeconds(2))
                setReadTimeout(Duration.ofSeconds(10))
            }
        return entraIDRestClientFactory
            .lagOboRestKlient(scope) {
                TokenContextHolder.getBearerToken("azuread")
                    ?: throw IllegalStateException("Kall mot Tilgangsmaskinen krever OBO-token, men fant ingen JWT i konteksten")
            }.mutate()
            .requestFactory(requestFactory)
            .defaultHeaders { it.contentType = MediaType.APPLICATION_JSON }
            .build()
    }
}
