package no.nav.gravferd.sak.adapter.inn

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SakOppslagController(
    private val jdbcTemplate: JdbcTemplate,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/api/sak/oppslag")
    fun hentSaker(
        @RequestParam fnr: String,
    ): List<Map<String, Any?>> {
        logger.info("Henter saker for fnr=$fnr")
        return jdbcTemplate.queryForList("SELECT * FROM sak WHERE soker_fnr = '$fnr'")
    }
}
