package org.coner.worker.view

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.Test
import org.testfx.api.FxAssert.verifyThat
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

    @Test
    fun shouldContainLogo() {
        verifyThat("#logo", isNotNull())
    }

    @Test
    fun shouldContainWorkerText() {
        verifyThat("#title_short", hasText("Worker"))
    }
}