package no.nav.gravferd.sak.inn.rest

import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(IkkeFunnetException::class)
    fun håndterIkkeFunnet(): ProblemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Fant ikke ressursen")
}
