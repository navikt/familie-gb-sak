package no.nav.gravferd.sak.config

import no.nav.gravferd.sak.fake.FakeLeaderClientService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
class FakeConfig {
    @Bean
    @Primary
    @Profile("mock-leader-client")
    fun fakeLeaderClientService(): FakeLeaderClientService = FakeLeaderClientService()
}
