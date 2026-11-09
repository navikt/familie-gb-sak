package no.nav.gravferd.sak.config.implementasjon

import no.nav.familie.leader.LeaderClient
import no.nav.gravferd.sak.inn.jobb.LeaderService
import org.springframework.stereotype.Service

@Service
class DefaultLeaderService : LeaderService {
    override fun isLeader(): Boolean = LeaderClient.isLeader() == true
}
