package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.AbstractIntegrasjonstest
import no.nav.gravferd.sak.fake.FakeClockProvider.Companion.STANDARDTIDSPUNKT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DefaultFagsakRepositoryTest : AbstractIntegrasjonstest() {
    @Autowired
    private lateinit var fagsakRepository: DefaultFagsakRepository

    @Test
    fun `opprettet fagsak kan hentes med sporing`() {
        // Arrange
        val fagsak = fagsakRepository.opprett()

        // Act
        val hentet = fagsakRepository.hent(fagsak.id)

        // Assert
        assertThat(hentet).isEqualTo(fagsak)
        assertThat(hentet?.opprettetAv).isEqualTo("VL")
        assertThat(hentet?.opprettetTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
        assertThat(hentet?.endretAv).isEqualTo("VL")
        assertThat(hentet?.endretTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
    }

    @Test
    fun `hver fagsak får egen id`() {
        // Act
        val første = fagsakRepository.opprett()
        val andre = fagsakRepository.opprett()

        // Assert
        assertThat(første.id).isNotEqualTo(andre.id)
    }

    @Test
    fun `hent gir null når fagsaken ikke finnes`() {
        // Act
        val hentet = fagsakRepository.hent(Long.MAX_VALUE)

        // Assert
        assertThat(hentet).isNull()
    }
}
