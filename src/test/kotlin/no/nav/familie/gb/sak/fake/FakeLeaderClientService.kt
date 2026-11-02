package no.nav.familie.gb.sak.fake

import no.nav.familie.gb.sak.config.LeaderClientService

class FakeLeaderClientService : LeaderClientService {
    override fun isLeader(): Boolean = true
}
