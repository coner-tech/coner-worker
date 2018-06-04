package org.coner.worker

import com.google.inject.Guice
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javafx.stage.WindowEvent
import org.coner.worker.di.MockkProcessModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.*
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KClass

class AppLifecycleControllerTest {

    lateinit var controller: AppLifecycleController

    private lateinit var robot: FxRobot

    @Before
    fun before() {
        val app = App()
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }
        FX.dicontainer = dicontainer
        robot = FxRobot()
        controller = find(AppLifecycleController::class)
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(controller.app)
        FX.dicontainer = null
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessNotStartedItShouldNotAlert() {
        every { controller.conerCoreProcess.started }.returns(false)
        val windowEvent: WindowEvent = mockk()

        controller.onCloseRequest(windowEvent)

        verify { controller.conerCoreProcess.started }
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessStartedItShouldAlert() {
        every { controller.conerCoreProcess.started }.returns(true)
        val windowEvent: WindowEvent = mockk()
        val latch = CountDownLatch(1)
        every { windowEvent.consume() }.answers { latch.countDown() }

        runLater { controller.onCloseRequest(windowEvent) }
        runLater { robot.clickOn("Cancel") }

        latch.await()
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessStartedClickOkItShouldStopProcess() {
        every { controller.conerCoreProcess.started }.returns(true)
        val windowEvent: WindowEvent = mockk()

        runLater { }
    }
}

private val dicontainer = object : DIContainer {
    val guice = Guice.createInjector(MockkProcessModule())

    override fun <T : Any> getInstance(type: KClass<T>): T {
        return guice.getInstance(type.java)
    }
}
