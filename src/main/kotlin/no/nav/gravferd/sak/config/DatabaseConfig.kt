package no.nav.gravferd.sak.config

import no.nav.familie.prosessering.PropertiesWrapperTilStringConverter
import no.nav.familie.prosessering.StringTilPropertiesWrapperConverter
import no.nav.gravferd.sak.common.ClockProvider
import no.nav.gravferd.sak.config.sikkerhet.SikkerhetContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import java.time.LocalDateTime
import java.util.Optional

@Configuration
@EnableJdbcRepositories("no.nav.familie.prosessering", "${ApplicationConfig.PAKKENAVN}.ut.db")
@EnableJdbcAuditing(
    auditorAwareRef = "auditorAware",
    dateTimeProviderRef = "auditingDateTimeProvider",
)
class DatabaseConfig : AbstractJdbcConfiguration() {
    @Bean
    override fun jdbcCustomConversions(): JdbcCustomConversions =
        JdbcCustomConversions(
            listOf(
                PropertiesWrapperTilStringConverter(),
                StringTilPropertiesWrapperConverter(),
            ),
        )

    @Bean
    fun auditorAware(): AuditorAware<String> = AuditorAware { Optional.of(SikkerhetContext.hentSaksbehandler()) }

    @Bean
    fun auditingDateTimeProvider(clockProvider: ClockProvider): DateTimeProvider =
        DateTimeProvider { Optional.of(LocalDateTime.now(clockProvider.get())) }
}
