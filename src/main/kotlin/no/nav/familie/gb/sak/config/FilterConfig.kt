package no.nav.familie.gb.sak.config

import no.nav.familie.log.NavSystemtype
import no.nav.familie.log.filter.LogFilter
import no.nav.familie.log.filter.RequestTimeFilter
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {
    @Bean
    fun logFilter(): FilterRegistrationBean<LogFilter> {
        log.info("Registering LogFilter filter")
        val filterRegistration: FilterRegistrationBean<LogFilter> = FilterRegistrationBean()
        filterRegistration.setFilter(LogFilter(NavSystemtype.NAV_SAKSBEHANDLINGSSYSTEM))
        filterRegistration.order = 1
        return filterRegistration
    }

    @Bean
    fun requestTimeFilter(): FilterRegistrationBean<RequestTimeFilter> {
        log.info("Registering RequestTimeFilter")
        val filterRegistration = FilterRegistrationBean<RequestTimeFilter>()
        filterRegistration.setFilter(RequestTimeFilter())
        filterRegistration.order = 2
        return filterRegistration
    }

    companion object {
        private val log = LoggerFactory.getLogger(FilterConfig::class.java)
    }
}
