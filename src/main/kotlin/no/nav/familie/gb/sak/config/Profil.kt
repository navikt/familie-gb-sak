package no.nav.familie.gb.sak.config

import org.springframework.core.env.Environment

fun Environment.erAktiv(profil: Profil) = activeProfiles.any { it == profil.navn.trim() }

enum class Profil(
    val navn: String,
) {
    DevPostgresPreprod("dev-postgres-preprod"),
    Prod("prod"),
    Preprod("preprod"),
}
