package no.nav.gravferd.sak.config.implementasjon

import no.nav.familie.unleash.UnleashService
import no.nav.gravferd.sak.kjerne.featuretoggle.FeatureToggle
import no.nav.gravferd.sak.kjerne.featuretoggle.FeatureToggleService
import org.springframework.stereotype.Component

@Component
class DefaultFeatureToggleService(
    private val unleashService: UnleashService,
) : FeatureToggleService {
    override fun erPå(
        toggle: FeatureToggle,
        default: Boolean,
    ): Boolean = unleashService.isEnabled(toggle.navn, default)
}
