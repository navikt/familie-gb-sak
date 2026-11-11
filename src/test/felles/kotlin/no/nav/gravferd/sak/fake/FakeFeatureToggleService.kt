package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.kjerne.featuretoggle.FeatureToggle
import no.nav.gravferd.sak.kjerne.featuretoggle.FeatureToggleService

class FakeFeatureToggleService : FeatureToggleService {
    private val toggles = ThreadLocal.withInitial { mutableMapOf<FeatureToggle, Boolean>() }

    override fun erPå(
        toggle: FeatureToggle,
        default: Boolean,
    ): Boolean = toggles.get()[toggle] ?: default

    fun settToggle(
        toggle: FeatureToggle,
        erPå: Boolean,
    ) {
        toggles.get()[toggle] = erPå
    }

    fun nullstill() = toggles.remove()
}
