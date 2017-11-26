package org.coner.worker.view

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.TextMatchers

class LogoViewTest : ApplicationTest() {

    private lateinit var logoView: LogoView

    override fun start(stage: Stage) {
        super.start(stage)
        logoView = LogoView()
        stage.scene = Scene(logoView.root)
        stage.show()
    }

    @Test
    fun shouldContainLogo() {
        FxAssert.verifyThat("#logo", NodeMatchers.isNotNull())
    }

    @Test
    fun shouldContainWorkerText() {
        FxAssert.verifyThat("#title_short", TextMatchers.hasText("Worker"))
    }
}