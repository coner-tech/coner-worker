package org.coner.worker.controller

import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import javafx.beans.property.SimpleObjectProperty
import org.assertj.core.api.Assertions.assertThat
import org.coner.worker.model.EasyModeModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import tornadofx.*
import java.util.concurrent.CountDownLatch

class EasyModeControllerTest {

    lateinit var easyMode: EasyModeController

    lateinit var model: EasyModeModel
    lateinit var conerCoreProcessController: ConerCoreProcessController

    @Before
    fun before() {
        val app = App()
        with(app.scope) {
            set(mockk<ConerCoreProcessController>(name = "conerCoreProcessController", relaxed = true))
        }
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }
        easyMode = find()

        model = find()
        conerCoreProcessController = find()
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(easyMode.app)
    }

    @Test
    fun itShouldStart() {
        val stepProperty = SimpleObjectProperty<EasyModeController.StartStep?>()
        val actualSteps: MutableList<EasyModeController.StartStep?> = mutableListOf()
        val latch = CountDownLatch(3)
        stepProperty.addListener { observable, oldValue, newValue ->
            actualSteps.add(newValue)
            latch.countDown()
        }

        easyMode.start(stepProperty)

        latch.await()

        verifyOrder {
            conerCoreProcessController.resolve()
            conerCoreProcessController.start()
            conerCoreProcessController.checkHealth()
        }
        assertThat(actualSteps).containsExactly(
                EasyModeController.StartStep.RESOLVE,
                EasyModeController.StartStep.START,
                EasyModeController.StartStep.HEALTH_CHECK,
                null
        )
        assertThat(model.started).isTrue()
    }

    @Test
    fun itShouldStop() {
        easyMode.stop()

        verify { conerCoreProcessController.stop() }
        assertThat(model.started).isFalse()
    }

    @Test
    fun itShouldCheckHealth() {
        easyMode.checkHealth()

        verify { conerCoreProcessController.checkHealth() }
    }

    @Test
    fun itShouldBuildConnectionPreferences() {
        val actual = easyMode.buildConnectionPreferences()

        assertThat(actual).isNotNull

    }
}