package no.nav.gravferd.sak.ut.db.behandling

import no.nav.gravferd.sak.AbstractIntegrasjonstest
import no.nav.gravferd.sak.config.sikkerhet.gyldigeClaims
import no.nav.gravferd.sak.config.sikkerhet.settSecurityHolderContext
import no.nav.gravferd.sak.fake.FakeClockProvider.Companion.STANDARDTIDSPUNKT
import no.nav.gravferd.sak.ut.db.fagsak.DefaultFagsakRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.context.SecurityContextHolder

class DefaultBehandlingRepositoryTest : AbstractIntegrasjonstest() {
    @Autowired
    private lateinit var fagsakRepository: DefaultFagsakRepository

    @Autowired
    private lateinit var behandlingRepository: DefaultBehandlingRepository

    @AfterEach
    fun ryddOpp() = SecurityContextHolder.clearContext()

    @Nested
    inner class Opprett {
        @Test
        fun `lagrer behandlingen med sporing fra systemet`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            val behandling = behandlingRepository.opprett(fagsak.id)

            // Act
            val hentet = behandlingRepository.hent(behandling.id)

            // Assert
            assertThat(hentet).isEqualTo(behandling)
            assertThat(hentet?.fagsakId).isEqualTo(fagsak.id)
            assertThat(hentet?.opprettetAv).isEqualTo("VL")
            assertThat(hentet?.opprettetTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
            assertThat(hentet?.endretAv).isEqualTo("VL")
            assertThat(hentet?.endretTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
        }

        @Test
        fun `lagrer innlogget saksbehandler som oppretter`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            settSecurityHolderContext(claims = gyldigeClaims + ("NAVident" to "Z123456"))

            // Act
            val behandling = behandlingRepository.opprett(fagsak.id)

            // Assert
            val hentet = behandlingRepository.hent(behandling.id)
            assertThat(hentet?.opprettetAv).isEqualTo("Z123456")
            assertThat(hentet?.endretAv).isEqualTo("Z123456")
        }

        @Test
        fun `feiler når fagsaken ikke finnes`() {
            // Act
            val feil = catchThrowable { behandlingRepository.opprett(Long.MAX_VALUE) }

            // Assert
            assertThat(feil).isInstanceOf(DataIntegrityViolationException::class.java)
        }
    }

    @Nested
    inner class HentForFagsak {
        @Test
        fun `gir bare behandlingene til fagsaken`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            val annenFagsak = fagsakRepository.opprett()
            val første = behandlingRepository.opprett(fagsak.id)
            val andre = behandlingRepository.opprett(fagsak.id)
            behandlingRepository.opprett(annenFagsak.id)

            // Act
            val behandlinger = behandlingRepository.hentForFagsak(fagsak.id)

            // Assert
            assertThat(behandlinger).containsExactlyInAnyOrder(første, andre)
        }

        @Test
        fun `gir tom liste når fagsaken ikke har behandlinger`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()

            // Act
            val behandlinger = behandlingRepository.hentForFagsak(fagsak.id)

            // Assert
            assertThat(behandlinger).isEmpty()
        }
    }

    @Nested
    inner class Hent {
        @Test
        fun `gir null når behandlingen ikke finnes`() {
            // Act
            val hentet = behandlingRepository.hent(Long.MAX_VALUE)

            // Assert
            assertThat(hentet).isNull()
        }
    }
}
