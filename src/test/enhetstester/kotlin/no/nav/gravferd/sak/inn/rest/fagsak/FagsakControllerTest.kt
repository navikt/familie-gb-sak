package no.nav.gravferd.sak.inn.rest.fagsak

import no.nav.gravferd.sak.fake.FakeFagsakRepository
import no.nav.gravferd.sak.inn.rest.ApiExceptionHandler
import no.nav.gravferd.sak.kjerne.fagsak.FagsakService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class FagsakControllerTest {
    private val fagsakService = FagsakService(FakeFagsakRepository())
    private val mockMvc: MockMvc =
        MockMvcBuilders
            .standaloneSetup(FagsakController(fagsakService))
            .setControllerAdvice(ApiExceptionHandler())
            .build()

    @Nested
    inner class Opprett {
        @Test
        fun `gir 201 med fagsaken`() {
            // Act & Assert
            mockMvc
                .perform(post("/api/fagsak"))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").isNumber)
                .andExpect(jsonPath("$.versjon").value(1))
                .andExpect(jsonPath("$.opprettetAv").value("VL"))
                .andExpect(jsonPath("$.opprettetTidspunkt").value("2026-01-01T12:00:00"))
                .andExpect(jsonPath("$.endretAv").value("VL"))
                .andExpect(jsonPath("$.endretTidspunkt").value("2026-01-01T12:00:00"))
        }
    }

    @Nested
    inner class Hent {
        @Test
        fun `gir fagsaken`() {
            // Arrange
            val fagsak = fagsakService.opprett()

            // Act & Assert
            mockMvc
                .perform(get("/api/fagsak/${fagsak.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(fagsak.id))
        }

        @Test
        fun `med id som ikke er et tall gir 400`() {
            // Act & Assert
            mockMvc
                .perform(get("/api/fagsak/abc"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `gir 404 når fagsaken ikke finnes`() {
            // Act & Assert
            mockMvc
                .perform(get("/api/fagsak/404"))
                .andExpect(status().isNotFound)
        }
    }
}
