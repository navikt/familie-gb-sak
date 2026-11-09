package no.nav.gravferd.sak.kjerne.featuretoggle

interface FeatureToggleService {
    fun erPå(
        toggle: FeatureToggle,
        default: Boolean = false,
    ): Boolean
}
