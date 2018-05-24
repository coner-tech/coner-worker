package org.coner.worker.screen.establish_connection

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.coner.core.client.ApiException
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
            set(ServiceConnectionModel())
            set(ConnectionPreferencesModel())
            set(mockk<CustomConnectionController>(relaxed = true))
        }
        FxToolkit.setupApplication { app }
        view = stage.uiComponent()!!
        page = ConerCoreServiceConnectionDetailsPage(FxRobot(), view)
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }

    @Test
    fun itShouldStartWithDefaultValues() {
        FxAssert.verifyThat(page.protocol) { it.value == null }
        FxAssert.verifyThat(page.host, TextInputControlMatchers.hasText(null as String?))
        FxAssert.verifyThat(page.applicationPort, TextInputControlMatchers.hasText("0"))
        FxAssert.verifyThat(page.adminPort, TextInputControlMatchers.hasText("0"))
        FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldEnableConnectWhenPageFilledRealisticValues() {
        page.fillRealisticValues()

        FxAssert.verifyThat(page.connect, NodeMatchers.isEnabled())
    }

    @Test
    fun itShouldEnableConnectWhenModelFilledRealisticValues() {
        view.model.fillRealisticValues()
    }

    @Test
    fun itShouldDisableConnectWhenHostEmpty() {
        view.model.fillRealisticValues()

        page.clearHost()

        FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldStripWhitespaceFromHost() {
        page.setHost("foo bar")

        FxAssert.verifyThat(page.host, TextInputControlMatchers.hasText("foobar"))
    }

    @Test
    fun itShouldDisableConnectWhenApplicationPortEmpty() {
        view.model.fillRealisticValues()

        page.clearApplicationPort()

        FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldStripNonIntegerFromApplicationPort() {
        page.setApplicationPort("a.!@1234")

        FxAssert.verifyThat(page.applicationPort, TextInputControlMatchers.hasText("1234"))
    }

    @Test
    fun itShouldDisableConnectWhenAdminPortEmpty() {
        view.model.fillRealisticValues()

        page.clearAdminPort()

        FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldStripNonIntegerFromAdminPort() {
        page.setAdminPort("a.!@#1234")

        FxAssert.verifyThat(page.adminPort, TextInputControlMatchers.hasText("1234"))
    }

    @Test
    fun itShouldAttemptToConnectWhenClickConnect() {
        val latch = CountDownLatch(1)
        every { view.controller.connect(any()) }.answers { latch.countDown() }
        view.model.fillRealisticValues()

        page.connect()

        latch.await()
        val specSlot = slot<AttemptCustomConerCoreConnection>()
        verify { view.controller.connect(capture(specSlot)) }
        assertEquals(page.realisticValues.applicationUri, specSlot.captured.applicationUri)
        assertEquals(page.realisticValues.adminUri, specSlot.captured.adminUri)
    }

    @Test
    fun itShouldNotifyControllerWhenConnectSucceeds() {
        val latch = CountDownLatch(2)
        every { view.controller.connect(any()) }.answers { latch.countDown() }
        every { view.controller.onConnectSuccess(any()) }.answers { latch.countDown() }
        view.model.fillRealisticValues()

        page.connect()

        latch.await()
        val specSlot = slot<AttemptCustomConerCoreConnection>()
        verify { view.controller.connect(capture(specSlot)) }
        verify { view.controller.onConnectSuccess(match { it == specSlot.captured }) }
    }

    @Test
    fun itShouldNotifyControllerWhenConnectFails() {
        val latch = CountDownLatch(1)
        every { view.controller.connect(any()) }.throws(ApiException())
        every { view.controller.onConnectFail(any()) }.answers { latch.countDown() }
        view.model.fillRealisticValues()

        page.connect()

        latch.await()
        val specSlot = slot<AttemptCustomConerCoreConnection>()
        verify { view.controller.connect(capture(specSlot)) }
        verify { view.controller.onConnectFail(match { it == specSlot.captured }) }
    }

    private fun ServiceConnectionModel.fillRealisticValues() {
        FX.runAndWait {
            setToRealisticValues(page.realisticValues)
        }
    }
}

private fun ServiceConnectionModel.setToRealisticValues(realisticValues: ConerCoreServiceConnectionDetailsPage.RealisticValues) {
    with(realisticValues) {
        protocol.value = applicationUri.scheme
        host.value = applicationUri.host
        applicationPort.value = applicationUri.port
        adminPort.value = adminUri.port
    }
}