package no.nav.gravferd.sak.kjerne.featuretoggle

interface FeatureToggleService {
    fun erPå(toggle: FeatureToggle): Boolean

    fun erPåForBehandling(
        toggle: FeatureToggle,
        behandlingId: Long,
    ): Boolean

    fun erPåForFagsak(
        toggle: FeatureToggle,
        fagsakId: Long,
    ): Boolean
}
