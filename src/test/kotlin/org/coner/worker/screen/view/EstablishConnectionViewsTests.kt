package org.coner.worker.screen.view

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.coner.core.client.ApiException
import org.coner.worker.page.ConerCoreServiceConnectionDetailsPage
import org.coner.worker.screen.establish_connection.CustomConnectionController
import org.coner.worker.screen.establish_connection.AttemptCustomConerCoreConnection
import org.coner.worker.screen.establish_connection.CustomConnectionView
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.TextInputControlMatchers
import tornadofx.*
import java.net.URI
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals

class CustomConnectionViewTest {

    lateinit var robot: FxRobot
    lateinit var app: App
    lateinit var page: ConerCoreServiceConnectionDetailsPage
    lateinit var controller: CustomConnectionController

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 600.0
        controller = mockk(relaxed = true)
        app = App(CustomConnectionView::class)
        app.scope.set(controller)
        FxToolkit.setupApplication { app }
        robot = FxRobot()
        page = ConerCoreServiceConnectionDetailsPage(robot, stage.uiComponent()!!)
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(app)
    }

    @Test
    fun itShouldStartWithDefaultValues() {
        // TODO: verify protocol
        FxAssert.verifyThat(page.host, TextInputControlMatchers.hasText("localhost"))
        FxAssert.verifyThat(page.applicationPort, TextInputControlMatchers.hasText("8080"))
        FxAssert.verifyThat(page.adminPort, TextInputControlMatchers.hasText("8081"))
    }

    @Test
    fun itShouldStartWithConnectEnabled() {
        FxAssert.verifyThat(page.connect, NodeMatchers.isEnabled())
    }

    @Test
    fun itShouldDisableConnectWhenHostEmpty() {
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
        every { controller.connect(any()) }.answers { latch.countDown() }

        page.connect()

        latch.await()
        var specSlot = slot<AttemptCustomConerCoreConnection>()
        verify { controller.connect(capture(specSlot)) }
        assertEquals(URI("http://localhost:8080"), specSlot.captured.applicationUri)
        assertEquals(URI("http://localhost:8081"), specSlot.captured.adminUri)
    }

    @Test
    fun itShouldNotifyControllerWhenConnectSucceeds() {
        val latch = CountDownLatch(2)
        every { controller.connect(any()) }.answers { latch.countDown() }
        every { controller.onConnectSuccess(any()) }.answers { latch.countDown() }

        page.connect()

        latch.await()
        var specSlot = slot<AttemptCustomConerCoreConnection>()
        verify { controller.connect(capture(specSlot)) }
        verify { controller.onConnectSuccess(match { it == specSlot.captured }) }
    }

    @Test
    fun itShouldNotifyControllerWhenConnectFails() {
        val latch = CountDownLatch(1)
        every { controller.connect(any()) }.throws(ApiException())
        every { controller.onConnectFail(any()) }.answers { latch.countDown() }

        page.connect()

        latch.await()
        val specSlot = slot<AttemptCustomConerCoreConnection>()
        verify { controller.connect(capture(specSlot)) }
        verify { controller.onConnectFail(match { it == specSlot.captured }) }
    }

}