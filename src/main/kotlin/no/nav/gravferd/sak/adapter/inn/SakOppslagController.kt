package no.nav.gravferd.sak.adapter.inn

import no.nav.familie.tilgangsmaskin.TilgangsmaskinKlient
import no.nav.gravferd.sak.common.secureLogger
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

data class SakOppslagRequest(
    val fnr: String,
)

@RestController
class SakOppslagController(
    private val jdbcTemplate: JdbcTemplate,
    private val tilgangsmaskinKlient: TilgangsmaskinKlient,
) {
    @PostMapping("/api/sak/oppslag")
    @PreAuthorize("hasAnyRole('SAKSBEHANDLER', 'BESLUTTER', 'VEILEDER')")
    fun hentSaker(
        @RequestBody request: SakOppslagRequest,
    ): List<Map<String, Any?>> {
        val tilgang = tilgangsmaskinKlient.sjekkTilgangTilPersoner(setOf(request.fnr)).single()
        if (!tilgang.harTilgang) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Mangler tilgang til personen")
        }

        secureLogger.info("Henter saker for fnr=${request.fnr}")
        return jdbcTemplate.queryForList("SELECT * FROM sak WHERE soker_fnr = ?", request.fnr)
    }
}
