package no.nav.gravferd.sak.common

import java.time.Clock

fun interface ClockProvider {
    fun get(): Clock
}
