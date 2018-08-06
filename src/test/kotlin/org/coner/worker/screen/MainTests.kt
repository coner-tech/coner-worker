package org.coner.worker.screen

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import javafx.stage.WindowEvent
import javafx.util.Duration
import org.coner.worker.ConnectionPreferencesController
import org.coner.worker.controller.EasyModeController
import org.coner.worker.di.GuiceDiContainer
import org.coner.worker.di.MockkProcessModule
import org.coner.worker.model.EasyModeModel
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
            set(mockk<EasyModeController>(relaxed = true))
            set(mockk<EasyModeModel>(relaxed = true))
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
            set(mockk<EasyModeController>(relaxed = true))
            set(mockk<EasyModeModel>(relaxed = true))
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
        every { controller.easyMode.model.started }.returns(false)
        val windowEvent: WindowEvent = mockk()

        controller.onCloseRequest(windowEvent)

        verify { controller.easyMode.model.started }
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessStartedItShouldAlert() {
        every { controller.easyMode.model.started }.returns(true)
        val windowEvent: WindowEvent = mockk()

        controller.onCloseRequest(windowEvent)

        verify { find<MainView>().showCloseRequestConfirmation(any(), any()) }
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessStartedUserConfirmsItShouldStopProcess() {
        val windowEvent: WindowEvent = mockk()
        every { controller.easyMode.model.started }.returns(true)
        val onConfirmedSlot = slot<() -> Unit>()
        val onCancelledSlot = slot<() -> Unit>()
        every {
            find<MainView>().showCloseRequestConfirmation(
                    onConfirmed = capture(onConfirmedSlot),
                    onCancelled = capture(onCancelledSlot)
            )
        }.answers { onConfirmedSlot.captured() }
        every { controller.easyMode.stop() }.answers { }

        controller.onCloseRequest(windowEvent)

        val easyModeController = find<EasyModeController>()
        verify { easyModeController.stop() }
    }

    @Test
    fun whenCloseRequestedAndConerCoreProcessStartedUserCancelsItShouldNotStopProcess() {
        val windowEvent: WindowEvent = mockk()
        every { controller.easyMode.model.started }.returns(true)
        val onConfirmedSlot = slot<() -> Unit>()
        val onCancelledSlot = slot<() -> Unit>()
        every {
            find<MainView>().showCloseRequestConfirmation(
                    onConfirmed = capture(onConfirmedSlot),
                    onCancelled = capture(onCancelledSlot)
            )
        }.answers { onCancelledSlot.captured() }
        every { windowEvent.consume() }.answers {  }

        controller.onCloseRequest(windowEvent)

        val easyModeController = find<EasyModeController>()
        verify(exactly = 0) { easyModeController.stop() }
    }
}
