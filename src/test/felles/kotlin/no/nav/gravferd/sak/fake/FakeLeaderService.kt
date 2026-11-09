package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.inn.jobb.LeaderService

class FakeLeaderService : LeaderService {
    override fun isLeader(): Boolean = true
}
