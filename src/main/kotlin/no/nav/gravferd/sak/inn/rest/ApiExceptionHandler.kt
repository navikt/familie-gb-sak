package no.nav.gravferd.sak.inn.rest

import no.nav.gravferd.sak.kjerne.feil.BaseFeil
import no.nav.gravferd.sak.kjerne.feil.Feil
import no.nav.gravferd.sak.kjerne.feil.FunksjonellFeil
import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetFeil
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(IkkeFunnetFeil::class)
    fun håndterIkkeFunnet(feil: IkkeFunnetFeil): ProblemDetail {
        logger.info(feil.melding)
        return tilProblemDetail(HttpStatus.NOT_FOUND, feil)
    }

    @ExceptionHandler(FunksjonellFeil::class)
    fun håndterFunksjonellFeil(feil: FunksjonellFeil): ProblemDetail {
        logger.info(feil.melding)
        return tilProblemDetail(HttpStatus.BAD_REQUEST, feil)
    }

    @ExceptionHandler(Feil::class)
    fun håndterFeil(feil: Feil): ProblemDetail {
        logger.error(feil.melding, feil)
        return tilProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, feil)
    }

    private fun tilProblemDetail(
        status: HttpStatus,
        feil: BaseFeil,
    ): ProblemDetail = ProblemDetail.forStatusAndDetail(status, feil.frontendFeilmelding)
}
