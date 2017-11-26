package org.coner.worker.view

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.control.TextMatchers

class LogoViewTest : ApplicationTest() {

    override fun init() {
        super.init()
        FxToolkit.registerStage { Stage() }
    }

    override fun start(stage: Stage) {
        super.start(stage)
        val logoView = LogoView()
        stage.scene = Scene(logoView.root)
        stage.show()
    }

    override fun stop() {
        super.stop()
        FxToolkit.hideStage()
    }

    @Test
    fun shouldContainWorkerText() {
        FxAssert.verifyThat(".text", TextMatchers.hasText("Worker"))
    }
}