package no.nav.gravferd.sak.inn.rest.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.FagsakService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/fagsak")
class FagsakController(
    private val fagsakService: FagsakService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun opprett(): FagsakDto = fagsakService.opprett().tilDto()

    @GetMapping("/{fagsakId}")
    fun hent(
        @PathVariable fagsakId: Long,
    ): FagsakDto = fagsakService.hent(fagsakId).tilDto()
}
