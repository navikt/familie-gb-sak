package no.nav.gravferd.sak.kjerne.behandling

import no.nav.gravferd.sak.fake.FakeBehandlingRepository
import no.nav.gravferd.sak.fake.FakeFagsakRepository
import no.nav.gravferd.sak.kjerne.fagsak.FagsakService
import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BehandlingServiceTest {
    private val fagsakService = FagsakService(FakeFagsakRepository())
    private val behandlingService = BehandlingService(FakeBehandlingRepository(), fagsakService)

    @Nested
    inner class Opprett {
        @Test
        fun `knytter behandlingen til fagsaken`() {
            // Arrange
            val fagsak = fagsakService.opprett()

            // Act
            val behandling = behandlingService.opprett(fagsak.id)

            // Assert
            assertThat(behandling.fagsakId).isEqualTo(fagsak.id)
            assertThat(behandlingService.hent(behandling.id)).isEqualTo(behandling)
        }

        @Test
        fun `kaster når fagsaken ikke finnes`() {
            // Act
            val feil = catchThrowable { behandlingService.opprett(404) }

            // Assert
            assertThat(feil).isInstanceOf(IkkeFunnetException::class.java)
        }
    }

    @Nested
    inner class Hent {
        @Test
        fun `kaster når behandlingen ikke finnes`() {
            // Act
            val feil = catchThrowable { behandlingService.hent(404) }

            // Assert
            assertThat(feil).isInstanceOf(IkkeFunnetException::class.java)
        }
    }

    @Nested
    inner class HentForFagsak {
        @Test
        fun `gir bare behandlingene til fagsaken`() {
            // Arrange
            val fagsak = fagsakService.opprett()
            val annenFagsak = fagsakService.opprett()
            val første = behandlingService.opprett(fagsak.id)
            val andre = behandlingService.opprett(fagsak.id)
            behandlingService.opprett(annenFagsak.id)

            // Act
            val behandlinger = behandlingService.hentForFagsak(fagsak.id)

            // Assert
            assertThat(behandlinger).containsExactlyInAnyOrder(første, andre)
        }

        @Test
        fun `gir tom liste når fagsaken ikke har behandlinger`() {
            // Arrange
            val fagsak = fagsakService.opprett()

            // Act
            val behandlinger = behandlingService.hentForFagsak(fagsak.id)

            // Assert
            assertThat(behandlinger).isEmpty()
        }

        @Test
        fun `kaster når fagsaken ikke finnes`() {
            // Act
            val feil = catchThrowable { behandlingService.hentForFagsak(404) }

            // Assert
            assertThat(feil).isInstanceOf(IkkeFunnetException::class.java)
        }
    }
}
