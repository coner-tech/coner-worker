package org.coner.worker

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers.isVisible

class WorkerAppIT {

    lateinit var app: WorkerApp

    @Before
    fun before() {
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication {
            app = WorkerApp()
            app.config.clear()
            app
        }
    }

    @After
    fun after() {
        app.config.clear()
        app.config.save()
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(app)
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