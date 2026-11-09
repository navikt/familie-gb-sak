package no.nav.gravferd.sak.config.implementasjon

import no.nav.gravferd.sak.common.ClockProvider
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class DefaultClockProvider : ClockProvider {
    override fun get(): Clock = Clock.systemDefaultZone()
}
