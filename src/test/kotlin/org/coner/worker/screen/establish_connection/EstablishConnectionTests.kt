package org.coner.worker.screen.establish_connection

import io.mockk.mockk
import io.mockk.verify
import org.coner.worker.page.EstablishConnectionPage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions
import tornadofx.*

class EstablishConnectionViewTest {

    lateinit var view: EstablishConnectionView
    lateinit var page: EstablishConnectionPage

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 600.0
        val app = App(EstablishConnectionView::class)
        with(app.scope) {
            set(mockk<EstablishConnectionController>(relaxed = true))
        }
        FxToolkit.setupApplication { app }
        view = stage.uiComponent()!!
        page = EstablishConnectionPage(FxRobot())
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }

    @Test
    fun onInitItShouldCallControllerNoOp() {
        // depends on view init during before()
        verify { view.controller.noOp() }
    }

    @Test
    fun itShouldHaveEasyModeTab() {
        page.clickEasyModeTab()

        Assertions.assertThat(page.tabs.selectionModel.selectedIndex)
                .isEqualTo(EstablishConnectionPage.Tabs.EasyMode.index)
    }

    @Test
    fun itShouldHaveCustomConnectionTab() {
        page.clickCustomConnectionTab()

        Assertions.assertThat(page.tabs.selectionModel.selectedIndex)
                .isEqualTo(EstablishConnectionPage.Tabs.Custom.index)
    }

    @Test
    fun itShouldTraversePages() {
        page.clickCustomConnectionTab()
        page.robot.clickOn(page.customPage.adminPort)
        page.clickEasyModeTab()
        page.robot.clickOn(page.easyModePage.pathToConfig)
    }
}