package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.config.LeaderClientService

class FakeLeaderClientService : LeaderClientService {
    override fun isLeader(): Boolean = true
}
