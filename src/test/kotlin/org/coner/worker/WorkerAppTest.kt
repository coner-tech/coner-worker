package org.coner.worker

import javafx.stage.Stage
import org.junit.Test
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers.isVisible

class WorkerAppTest : ApplicationTest() {

    override fun start(stage: Stage) {
        super.start(stage)
        WorkerApp().start(stage)
    }

    @Test
    fun shouldHaveLogoVisible() {
        verifyThat("#logo", isVisible())
    }
}