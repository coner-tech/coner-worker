package org.coner.worker.screen.establish_connection

import org.coner.worker.page.EasyModeConnectionPage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.*

class EasyModeConnectionViewTest {

    lateinit var view: EasyModeConnectionView
    lateinit var page: EasyModeConnectionPage

    @Rule
    @JvmField
    val folder = TemporaryFolder()

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 600.0
        val app = App(EasyModeConnectionView::class)
        with(app.scope) {
            // TODO: injections?
        }
        FxToolkit.setupApplication { app }
        view = stage.uiComponent()!!
        page = EasyModeConnectionPage(FxRobot())
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }
}
