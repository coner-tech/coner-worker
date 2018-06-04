package org.coner.worker.screen

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import javafx.stage.WindowEvent
import javafx.util.Duration
import org.coner.worker.ConnectionPreferencesController
import org.coner.worker.di.GuiceDiContainer
import org.coner.worker.di.MockkProcessModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.*

class MainViewTest {

    lateinit var view: MainView
    lateinit var robot: FxRobot

    @Before
    fun setup() {
        val app = App()
        FX.dicontainer = GuiceDiContainer(MockkProcessModule())
        with(app.scope) {
            set(mockk<MainController>(relaxed = true))
        }
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }
        view = find()
        robot = FxRobot()
    }

    @After
    fun after() {
        FX.dicontainer = null
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }

    @Test
    fun whenShowCloseRequestConfirmationUserClicksOkItShouldConfirm() {
        val onConfirmed = mockk<() -> Unit>()
        val onCancelled = mockk<() -> Unit>()
        every { onConfirmed() }.answers { }

        runLater(Duration.millis(500.0)) { robot.clickOn("OK") }
        FX.runAndWait { view.showCloseRequestConfirmation(onConfirmed, onCancelled) }

        verify { onConfirmed() }
    }

    @Test
    fun whenShowCloseRequestConfirmationUserClicksCancelItShouldCancel() {
        val onConfirmed = mockk<() -> Unit>()
        val onCancelled = mockk<() -> Unit>()
        every { onCancelled() }.answers { }

        runLater(Duration.millis(500.0)) { robot.clickOn("Cancel") }
        FX.runAndWait { view.showCloseRequestConfirmation(onConfirmed, onCancelled) }

        verify { onCancelled() }
    }
}

class MainControllerTest {

    lateinit var controller: MainController

    @Before
    fun setup() {
        val app = App()
        FX.dicontainer = GuiceDiContainer(MockkProcessModule())
        with(app.scope) {
            set(mockk<MainView>(relaxed = true))
            set(mockk<ConnectionPreferencesController>())
        }
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }
        controller = find()
    }

    @After
    fun after() {
        FX.dicontainer = null
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(controller.app)
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

        controller.onCloseRequest(windowEvent)

        verify { find<MainView>().showCloseRequestConfirmation(any(), any()) }
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessStartedUserConfirmsItShouldStopProcess() {
        val windowEvent: WindowEvent = mockk()
        every { controller.conerCoreProcess.started }.returns(true)
        val onConfirmedSlot = slot<() -> Unit>()
        val onCancelledSlot = slot<() -> Unit>()
        every {
            find<MainView>().showCloseRequestConfirmation(
                    onConfirmed = capture(onConfirmedSlot),
                    onCancelled = capture(onCancelledSlot)
            )
        }.answers { onConfirmedSlot.captured() }
        every { controller.conerCoreProcess.stop() }.answers { }

        controller.onCloseRequest(windowEvent)

        verify { controller.conerCoreProcess.stop() }
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessStartedUserCancelsItShouldNotStopProcess() {
        val windowEvent: WindowEvent = mockk()
        every { controller.conerCoreProcess.started }.returns(true)
        val onConfirmedSlot = slot<() -> Unit>()
        val onCancelledSlot = slot<() -> Unit>()
        every {
            find<MainView>().showCloseRequestConfirmation(
                    onConfirmed = capture(onConfirmedSlot),
                    onCancelled = capture(onCancelledSlot)
            )
        }.answers { onCancelledSlot.captured }
        every { controller.conerCoreProcess.stop() }.answers { }

        controller.onCloseRequest(windowEvent)

        verify(exactly = 0) { controller.conerCoreProcess.stop() }
    }
}
