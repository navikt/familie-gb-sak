package no.nav.gravferd.sak

import no.nav.gravferd.sak.fake.FakeClockProvider
import no.nav.gravferd.sak.fake.FakeFeatureToggleService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(classes = [Application::class])
@AutoConfigureMockMvc
@ActiveProfiles(
    "integrasjonstest",
    "mock-leader-client",
    "mock-clock-provider",
    "mock-feature-toggle",
)
@Tag("integration")
abstract class AbstractIntegrasjonstest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var fakeClockProvider: FakeClockProvider

    @Autowired
    protected lateinit var fakeFeatureToggleService: FakeFeatureToggleService

    @AfterEach
    fun nullstillFakes() {
        fakeClockProvider.nullstill()
        fakeFeatureToggleService.nullstill()
    }
}
