package org.coner.worker.screen.establish_connection

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.coner.core.client.ApiException
import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.page.ConerCoreServiceConnectionDetailsPage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.TextInputControlMatchers
import tornadofx.*
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals

class CustomConnectionViewTest {

    lateinit var view: CustomConnectionView
    lateinit var page: ConerCoreServiceConnectionDetailsPage

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 600.0
        val app = App(CustomConnectionView::class)
        with(app.scope) {
            set(CustomConnectionModel())
            set(ConnectionPreferencesModel())
            set(mockk<CustomConnectionController>(relaxed = true))
        }
        FxToolkit.setupApplication { app }
        view = stage.uiComponent()!!
        page = ConerCoreServiceConnectionDetailsPage(FxRobot())
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }

    @Test
    fun itShouldStartWithDefaultValues() {
        FxAssert.verifyThat(page.protocol) { it.value == "http" }
        FxAssert.verifyThat(page.host, TextInputControlMatchers.hasText("localhost"))
        FxAssert.verifyThat(page.applicationPort, TextInputControlMatchers.hasText("8080"))
        FxAssert.verifyThat(page.adminPort, TextInputControlMatchers.hasText("8081"))
        FxAssert.verifyThat(page.connect, NodeMatchers.isEnabled())
    }

    @Test
    fun itShouldEnableConnectWhenPageFilledRealisticValues() {
        page.fillRealisticValues()

        FX.runAndWait {
            FxAssert.verifyThat(page.connect, NodeMatchers.isEnabled())
        }
    }

    @Test
    fun itShouldEnableConnectWhenModelFilledRealisticValues() {
        view.model.fillRealisticValues()

        FX.runAndWait {
            FxAssert.verifyThat(page.connect, NodeMatchers.isEnabled())
        }
    }

    @Test
    fun itShouldDisableConnectWhenHostEmpty() {
        view.model.fillRealisticValues()

        page.clearHost()

        FX.runAndWait {
            FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
        }
    }

    @Test
    fun itShouldStripWhitespaceFromHost() {
        page.setHost("foo bar")

        FX.runAndWait {
            FxAssert.verifyThat(page.host, TextInputControlMatchers.hasText("foobar"))
        }
    }

    @Test
    fun itShouldDisableConnectWhenApplicationPortEmpty() {
        view.model.fillRealisticValues()

        page.clearApplicationPort()

        FX.runAndWait {
            FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
        }
    }

    @Test
    fun itShouldStripNonIntegerFromApplicationPort() {
        page.setApplicationPort("a.!@1234")

        FX.runAndWait {
            FxAssert.verifyThat(page.applicationPort, TextInputControlMatchers.hasText("1234"))
        }
    }

    @Test
    fun itShouldDisableConnectWhenAdminPortEmpty() {
        view.model.fillRealisticValues()

        page.clearAdminPort()

        FX.runAndWait {
            FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
        }
    }

    @Test
    fun itShouldStripNonIntegerFromAdminPort() {
        page.setAdminPort("a.!@#1234")

        FX.runAndWait {
            FxAssert.verifyThat(page.adminPort, TextInputControlMatchers.hasText("1234"))
        }
    }

    @Test
    fun itShouldAttemptToConnectWhenClickConnect() {
        val latch = CountDownLatch(1)
        every { view.controller.connect(any<AttemptCustomConerCoreConnection>()) }.answers { latch.countDown() }
        view.model.fillRealisticValues()

        page.connect()

        latch.await()
        FX.runAndWait {
            val specSlot = slot<AttemptCustomConerCoreConnection>()
            verify { view.controller.connect(capture(specSlot)) }
            assertEquals(page.realisticValues.applicationUri, specSlot.captured.applicationUri)
            assertEquals(page.realisticValues.adminUri, specSlot.captured.adminUri)
        }
    }

    @Test
    fun itShouldNotifyControllerWhenConnectSucceeds() {
        val latch = CountDownLatch(2)
        every { view.controller.connect(any<AttemptCustomConerCoreConnection>()) }.answers { latch.countDown() }
        every { view.controller.onConnectSuccess(any()) }.answers { latch.countDown() }
        view.model.fillRealisticValues()

        page.connect()

        latch.await()
        FX.runAndWait {
            val specSlot = slot<AttemptCustomConerCoreConnection>()
            verify { view.controller.connect(capture(specSlot)) }
            verify { view.controller.onConnectSuccess(match { it == specSlot.captured }) }
        }
    }

    @Test
    fun itShouldNotifyControllerWhenConnectFails() {
        val latch = CountDownLatch(1)
        every { view.controller.connect(any<AttemptCustomConerCoreConnection>()) }.throws(ApiException())
        every { view.controller.onConnectFail(any()) }.answers { latch.countDown() }
        view.model.fillRealisticValues()

        page.connect()

        latch.await()
        FX.runAndWait {
            val specSlot = slot<AttemptCustomConerCoreConnection>()
            verify { view.controller.connect(capture(specSlot)) }
            verify { view.controller.onConnectFail(match { it == specSlot.captured }) }
        }
    }

    private fun CustomConnectionModel.fillRealisticValues() {
        FX.runAndWait {
            setToRealisticValues(page.realisticValues)
        }
    }
}

private fun CustomConnectionModel.setToRealisticValues(realisticValues: ConerCoreServiceConnectionDetailsPage.RealisticValues) {
    with(realisticValues) {
        protocol = applicationUri.scheme
        host = applicationUri.host
        applicationPort = applicationUri.port
        adminPort = adminUri.port
    }
}

class CustomConnectionControllerTest {

    lateinit var controller: CustomConnectionController

    lateinit var page: ConerCoreServiceConnectionDetailsPage

    @Before
    fun before() {
        val app = App()
        with(app.scope) {
            set(mockk<CustomConnectionView>(relaxed = true))
        }
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }
        controller = find()
        page = ConerCoreServiceConnectionDetailsPage(FxRobot())
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(controller.app)
    }

    @Test
    fun whenOfferWrongConnectionPreferenceItShouldIgnore() {
        val wrong = ConnectionPreferences().apply {
            mode = ConnectionPreferences.Mode.EASY
        }

        val actual = controller.offer(wrong)

        assertThat(actual).isInstanceOf(SpecificEstablishConnectionController.OfferResult.Ignored::class.java)
        assertThat(controller.model.connectionPreferences).isNull()
    }

    @Test
    fun whenOfferRightConnectionPreferenceItShouldClaim() {
        val right = ConnectionPreferences().apply {
            mode = ConnectionPreferences.Mode.CUSTOM
            conerCoreServiceUrl = page.realisticValues.applicationUri
            conerCoreAdminUrl = page.realisticValues.adminUri
        }

        val actual = controller.offer(right)

        assertThat(actual).isInstanceOf(SpecificEstablishConnectionController.OfferResult.Claimed::class.java)
        assertThat(controller.model.connectionPreferences).isSameAs(right)
    }

    @Test
    fun whenConnectConnectionPreferencesItShouldPassToView() {
        val connectionPreferences: ConnectionPreferences = mockk()

        controller.connect(connectionPreferences)

        verify { controller.view.onClickConnectButton() }
    }
}