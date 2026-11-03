package no.nav.gravferd.sak.config.featureToggle

import no.nav.familie.unleash.UnleashService
import org.springframework.stereotype.Service

@Service
class FeatureToggleService(
    private val unleashService: UnleashService,
) {
    fun isEnabled(toggle: FeatureToggle): Boolean = unleashService.isEnabled(toggle.navn)

    fun isEnabled(
        toggle: FeatureToggle,
        defaultValue: Boolean,
    ): Boolean = unleashService.isEnabled(toggle.navn, defaultValue)
}
