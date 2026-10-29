package no.nav.familie.gb.sak.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling

@Profile("prod", "preprod")
@Configuration
@EnableScheduling
class SchedulingConfig
