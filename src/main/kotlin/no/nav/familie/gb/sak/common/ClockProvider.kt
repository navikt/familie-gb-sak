package no.nav.familie.gb.sak.common

import java.time.Clock

fun interface ClockProvider {
    fun get(): Clock
}
