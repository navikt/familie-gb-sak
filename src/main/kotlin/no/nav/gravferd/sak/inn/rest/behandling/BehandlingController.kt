package no.nav.gravferd.sak.inn.rest.behandling

import no.nav.gravferd.sak.kjerne.behandling.BehandlingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/behandling")
class BehandlingController(
    private val behandlingService: BehandlingService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun opprett(
        @RequestBody request: OpprettBehandlingRequest,
    ): BehandlingDto = behandlingService.opprett(request.fagsakId).tilDto()

    @GetMapping("/{behandlingId}")
    fun hent(
        @PathVariable behandlingId: Long,
    ): BehandlingDto = behandlingService.hent(behandlingId).tilDto()

    @GetMapping("/fagsak/{fagsakId}")
    fun hentForFagsak(
        @PathVariable fagsakId: Long,
    ): List<BehandlingDto> = behandlingService.hentForFagsak(fagsakId).map { it.tilDto() }
}
