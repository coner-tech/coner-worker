package org.coner.worker.screen.establish_connection

import com.authzee.kotlinguice4.getInstance
import com.google.inject.Guice
import com.google.inject.Injector
import io.mockk.mockk
import io.mockk.verify
import org.coner.worker.di.PageModule
import org.coner.worker.page.EstablishConnectionPage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions
import tornadofx.*

class EstablishConnectionViewTest {

    lateinit var view: EstablishConnectionView
    lateinit var page: EstablishConnectionPage

    lateinit var injector: Injector

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
        injector = Guice.createInjector(PageModule(
                navigationMenuParents = arrayOf(view.root)
        ))
        FX.runAndWait {
            page = injector.getInstance()
        }
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
    fun itShouldHaveEasyModeNav() {
        page.clickEasyModeNav()

        Assertions.assertThat(view.listMenuNav.activeItemIndex).isEqualTo(0)
    }

    @Test
    fun itShouldHaveCustomConnectionNav() {
        page.clickCustomConnectionNav()

        Assertions.assertThat(view.listMenuNav.activeItemIndex).isEqualTo(1)
    }

    @Test
    fun itShouldTraversePages() {
        page.clickCustomConnectionNav()
        page.robot.clickOn(page.customPage.adminPort)
        page.clickEasyModeNav()
    }
}