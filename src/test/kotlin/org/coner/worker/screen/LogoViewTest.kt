package org.coner.worker.screen

import javafx.scene.Scene
import javafx.stage.Stage
import org.coner.worker.screen.LogoView
import org.junit.Test
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxToolkit
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers.isNotNull
import org.testfx.matcher.control.TextMatchers.hasText

class LogoViewTest : ApplicationTest() {

    private lateinit var logoView: LogoView

    override fun start(stage: Stage) {
        super.start(stage)
        logoView = LogoView()
        stage.scene = Scene(logoView.root)
        stage.show()
    }

    override fun stop() {
        super.stop()
        FxToolkit.cleanupStages()
    }

    @Test
    fun shouldContainLogo() {
        verifyThat("#logo", isNotNull())
    }

    @Test
    fun shouldContainWorkerText() {
        verifyThat("#title_short", hasText("Worker"))
    }
}