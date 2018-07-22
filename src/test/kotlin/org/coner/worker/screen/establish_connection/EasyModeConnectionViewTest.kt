package org.coner.worker.screen.establish_connection

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javafx.concurrent.Task
import org.coner.worker.WorkerStylesheet
import org.coner.worker.controller.EasyModeController
import org.coner.worker.page.EasyModeConnectionPage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.*
import java.util.concurrent.CountDownLatch

class EasyModeConnectionViewTest {

    lateinit var view: EasyModeConnectionView

    lateinit var page: EasyModeConnectionPage

    lateinit var controller: EasyModeConnectionController
    lateinit var model: EasyModeConnectionModel
    lateinit var easyMode: EasyModeController

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 600.0
        val app = App(EasyModeConnectionView::class, WorkerStylesheet::class)
        with(app.scope) {
            set(mockk<EasyModeConnectionController>(name = "controller", relaxed = true))
            set(mockk<EasyModeController>(name = "easyMode", relaxed = true))
        }
        FxToolkit.setupApplication { app }
        view = stage.uiComponent()!!

        page = EasyModeConnectionPage(FxRobot())

        model = find()
        controller = find()
        easyMode = find()
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }

    @Test
    fun itShouldStartWithUseNodesVisibleAndButtonEnabled() {
        assertThat(page.useNodes).allSatisfy { assertThat(it).isVisible }
        assertThat(page.useButton).isVisible
    }

    @Test
    fun itShouldStartWithProgressWrapperInvisible() {
        assertThat(page.progressWrapper).isInvisible
    }

    @Test
    fun itShouldUseEasyModeAndHandleSuccess() {
        val latch = CountDownLatch(2)
        every { controller.useEasyMode() }.answers { latch.countDown() }
        every { controller.onUseEasyModeSuccess() }.answers { latch.countDown() }

        page.clickUseButton()

        latch.await()
        FX.runAndWait {
            verify { controller.useEasyMode() }
            verify { controller.onUseEasyModeSuccess()}
            org.assertj.core.api.Assertions.assertThat(model.useEasyModeTask).isNull()
        }
    }

    @Test
    fun itShouldUseEasyModeAndHandleFailure() {
        val latch = CountDownLatch(1)
        val expected = Exception()
        every { controller.useEasyMode() }.throws(expected)
        every { controller.onUseEasyModeFail(expected) }.answers { latch.countDown() }

        page.clickUseButton()

        latch.await()
        FX.runAndWait {
            verify { controller.useEasyMode() }
            verify { controller.onUseEasyModeFail(expected) }
            org.assertj.core.api.Assertions.assertThat(model.useEasyModeTask).isNull()
        }
    }

    @Test
    fun itShouldAssignUseEasyModeTaskWhenClicked() {
        val latch = CountDownLatch(1)
        val actualTasks: MutableList<Task<Unit>?> = mutableListOf()
        model.useEasyModeTaskProperty.onChange {
            actualTasks.add(it)
            latch.countDown()
        }

        page.clickUseButton()

        latch.await()
        FX.runAndWait {
            val assertActualTasks = org.assertj.core.api.Assertions.assertThat(actualTasks)
            assertActualTasks.hasSize(2)
            assertActualTasks.element(0).isNotNull
            assertActualTasks.element(1).isNull()
        }
    }

    @Test
    fun itShouldDisplayProgressWrapperNodesWhenUseEasyModeTaskIsNotNull() {
        model.useEasyModeTask = mockk()

        FX.runAndWait {
            assertThat(page.progressNodes).allSatisfy { assertThat(it).isVisible }
        }
    }

    @Test
    fun itShouldDisplayCorrectMessagesForEasyModeControllerStartSteps() {
        model.useEasyModeTask = mockk()

        FX.runAndWait {
            model.startStep = EasyModeController.StartStep.RESOLVE
            assertThat(page.progressStartStep).hasText(view.messages["use_easy_mode_progress_step_resolve"])
        }

        FX.runAndWait {
            model.startStep = EasyModeController.StartStep.START
            assertThat(page.progressStartStep).hasText(view.messages["use_easy_mode_progress_step_start"])
        }

        FX.runAndWait {
            model.startStep = EasyModeController.StartStep.HEALTH_CHECK
            assertThat(page.progressStartStep).hasText(view.messages["use_easy_mode_progress_step_health_check"])
        }
    }

}
