package org.coner.worker.screen.establish_connection

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.coner.worker.ConnectionPreferences
import org.coner.worker.controller.EasyModeController
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import tornadofx.*

class EasyModeConnectionControllerTest {

    lateinit var controller: EasyModeConnectionController

    lateinit var model: EasyModeConnectionModel
    lateinit var view: EasyModeConnectionView
    lateinit var easyMode: EasyModeController

    @Before
    fun before() {
        val app = App()
        with (app.scope) {
            set(mockk<EasyModeConnectionView>(name = "view", relaxed = true))
            set(mockk<EasyModeController>(name = "easyMode", relaxed = true))
        }
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }
        controller = find()

        model = find()
        view = find()
        easyMode = find()
    }

    @After
    fun after() {
        FxToolkit.cleanupApplication(controller.app)
    }

    @Test
    fun itShouldUseEasyMode() {
        controller.useEasyMode()

        verify { easyMode.start(model.startStepProperty) }
    }

    @Test
    fun itShouldHandleEasyModeSuccess() {
        val expectedConnectionPreferences: ConnectionPreferences = mockk()
        every { easyMode.buildConnectionPreferences() }.returns(expectedConnectionPreferences)


        controller.onUseEasyModeSuccess()

        verify { easyMode.buildConnectionPreferences() }
        assertThat(model.connectionPreferences).isSameAs(expectedConnectionPreferences)
    }

    @Test
    fun itShouldHandleEasyModeFailure() {
        val throwable: Throwable = mockk()

        controller.onUseEasyModeFail(throwable)

        verify { easyMode.stop() }
        verify { view.showUseEasyModeError(throwable) }
    }
}