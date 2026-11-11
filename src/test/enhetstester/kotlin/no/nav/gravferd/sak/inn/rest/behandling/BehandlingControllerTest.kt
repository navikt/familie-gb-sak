package no.nav.gravferd.sak.inn.rest.behandling

import no.nav.gravferd.sak.fake.FakeBehandlingRepository
import no.nav.gravferd.sak.fake.FakeFagsakRepository
import no.nav.gravferd.sak.inn.rest.ApiExceptionHandler
import no.nav.gravferd.sak.kjerne.behandling.BehandlingService
import no.nav.gravferd.sak.kjerne.fagsak.FagsakService
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class BehandlingControllerTest {
    private val fagsakService = FagsakService(FakeFagsakRepository())
    private val behandlingService = BehandlingService(FakeBehandlingRepository(), fagsakService)
    private val mockMvc: MockMvc =
        MockMvcBuilders
            .standaloneSetup(BehandlingController(behandlingService))
            .setControllerAdvice(ApiExceptionHandler())
            .build()

    @Nested
    inner class Opprett {
        @Test
        fun `gir 201 med behandlingen`() {
            // Arrange
            val fagsak = fagsakService.opprett()

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/behandling")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"fagsakId": ${fagsak.id}}"""),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").isNumber)
                .andExpect(jsonPath("$.fagsakId").value(fagsak.id))
                .andExpect(jsonPath("$.versjon").value(1))
                .andExpect(jsonPath("$.opprettetAv").value("VL"))
                .andExpect(jsonPath("$.opprettetTidspunkt").value("2026-01-01T12:00:00"))
        }

        @Test
        fun `uten fagsakId gir 400`() {
            // Act & Assert
            mockMvc
                .perform(
                    post("/api/behandling")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `gir 404 når fagsaken ikke finnes`() {
            // Act & Assert
            mockMvc
                .perform(
                    post("/api/behandling")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"fagsakId": 404}"""),
                ).andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class Hent {
        @Test
        fun `gir behandlingen`() {
            // Arrange
            val fagsak = fagsakService.opprett()
            val behandling = behandlingService.opprett(fagsak.id)

            // Act & Assert
            mockMvc
                .perform(get("/api/behandling/${behandling.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(behandling.id))
                .andExpect(jsonPath("$.fagsakId").value(fagsak.id))
        }

        @Test
        fun `gir 404 når behandlingen ikke finnes`() {
            // Act & Assert
            mockMvc
                .perform(get("/api/behandling/404"))
                .andExpect(status().isNotFound)
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

            // Act & Assert
            mockMvc
                .perform(get("/api/behandling/fagsak/${fagsak.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(første.id.toInt(), andre.id.toInt())))
        }

        @Test
        fun `gir 404 når fagsaken ikke finnes`() {
            // Act & Assert
            mockMvc
                .perform(get("/api/behandling/fagsak/404"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `gir tom liste når fagsaken ikke har behandlinger`() {
            // Arrange
            val fagsak = fagsakService.opprett()

            // Act & Assert
            mockMvc
                .perform(get("/api/behandling/fagsak/${fagsak.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(0))
        }
    }
}
