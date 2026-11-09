package no.nav.gravferd.sak.fake

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
class FakeConfig {
    @Bean
    @Primary
    @Profile("mock-leader-client")
    fun fakeLeaderService(): FakeLeaderService = FakeLeaderService()
}
