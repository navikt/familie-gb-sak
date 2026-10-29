package no.nav.familie.gb.sak.config

import no.nav.familie.leader.LeaderClient
import org.springframework.stereotype.Service

interface LeaderClientService {
    fun isLeader(): Boolean
}

@Service
class DefaultLeaderClientService : LeaderClientService {
    override fun isLeader(): Boolean = LeaderClient.isLeader() == true
}
