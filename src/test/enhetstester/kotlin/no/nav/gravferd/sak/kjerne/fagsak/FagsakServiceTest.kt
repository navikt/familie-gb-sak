package no.nav.gravferd.sak.kjerne.fagsak

import no.nav.gravferd.sak.fake.FakeFagsakRepository
import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test

class FagsakServiceTest {
    private val fagsakService = FagsakService(FakeFagsakRepository())

    @Test
    fun `hent gir fagsaken som ble opprettet`() {
        // Arrange
        val fagsak = fagsakService.opprett()

        // Act
        val hentet = fagsakService.hent(fagsak.id)

        // Assert
        assertThat(hentet).isEqualTo(fagsak)
    }

    @Test
    fun `hent kaster når fagsaken ikke finnes`() {
        // Act
        val feil = catchThrowable { fagsakService.hent(404) }

        // Assert
        assertThat(feil).isInstanceOf(IkkeFunnetException::class.java)
    }
}
