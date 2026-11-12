package no.nav.gravferd.sak.config.implementasjon

import no.nav.familie.unleash.UnleashContextFields.BEHANDLING_ID
import no.nav.familie.unleash.UnleashContextFields.EPOST
import no.nav.familie.unleash.UnleashContextFields.FAGSAK_ID
import no.nav.familie.unleash.UnleashContextFields.NAV_IDENT
import no.nav.familie.unleash.UnleashService
import no.nav.gravferd.sak.config.sikkerhet.SikkerhetContext.hentSaksbehandler
import no.nav.gravferd.sak.config.sikkerhet.SikkerhetContext.hentSaksbehandlerEpost
import no.nav.gravferd.sak.kjerne.featuretoggle.FeatureToggle
import no.nav.gravferd.sak.kjerne.featuretoggle.FeatureToggleService
import org.springframework.stereotype.Component

@Component
class DefaultFeatureToggleService(
    private val unleashService: UnleashService,
) : FeatureToggleService {
    override fun erPå(toggle: FeatureToggle): Boolean =
        erPå(
            toggle = toggle,
            properties = emptyMap(),
        )

    override fun erPåForBehandling(
        toggle: FeatureToggle,
        behandlingId: Long,
    ): Boolean =
        erPå(
            toggle = toggle,
            properties = mapOf(BEHANDLING_ID to behandlingId.toString()),
        )

    override fun erPåForFagsak(
        toggle: FeatureToggle,
        fagsakId: Long,
    ): Boolean =
        erPå(
            toggle = toggle,
            properties = mapOf(FAGSAK_ID to fagsakId.toString()),
        )

    private fun erPå(
        toggle: FeatureToggle,
        properties: Map<String, String>,
    ): Boolean =
        unleashService.isEnabled(
            toggleId = toggle.navn,
            properties =
                properties +
                    mapOf(
                        NAV_IDENT to hentSaksbehandler(),
                        EPOST to hentSaksbehandlerEpost(),
                    ),
        )
}
