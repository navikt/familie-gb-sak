package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.common.ClockProvider
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

class FakeClockProvider : ClockProvider {
    private val tid = ThreadLocal.withInitial { STANDARDTIDSPUNKT }

    override fun get(): Clock = Clock.fixed(tid.get().toInstant(ZoneOffset.UTC), ZoneOffset.UTC)

    fun settTid(tidspunkt: LocalDateTime) = tid.set(tidspunkt)

    fun nullstill() = tid.remove()

    companion object {
        val STANDARDTIDSPUNKT: LocalDateTime = LocalDateTime.of(2026, 1, 1, 12, 0)
    }
}
