package org.coner.worker

import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers.isVisible

class WorkerAppTest {

    companion object {
        val app = WorkerApp()

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            FxToolkit.registerPrimaryStage()
            FxToolkit.setupApplication { app }
        }

        @JvmStatic
        @AfterClass
        fun afterClass() {
            FxToolkit.cleanupStages()
            FxToolkit.cleanupApplication(app)
        }

    }

    @Before
    fun before() {
        app.config.clear()
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