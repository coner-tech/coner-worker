package org.coner.worker

import javafx.stage.Stage
import org.junit.Test
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers.isVisible

class WorkerAppTest : ApplicationTest() {

    lateinit var app: WorkerApp

    override fun start(stage: Stage) {
        super.start(stage)
        app = WorkerApp()
        app.config.clear()
        app.start(stage)
    }

    override fun stop() {
        super.stop()
        app.stop()
    }

    @Test
    fun shouldHaveLogoVisible() {
        verifyThat("#logo", isVisible())
    }

    @Test
    fun shouldHaveEstablishConnectionVisible() {
        verifyThat("#establish_connection", isVisible())
    }
}