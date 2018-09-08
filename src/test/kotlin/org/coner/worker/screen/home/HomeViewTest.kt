package org.coner.worker.screen.home

import com.authzee.kotlinguice4.getInstance
import com.google.inject.Guice
import org.coner.worker.di.PageModule
import org.coner.worker.page.HomePage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions
import tornadofx.*

class HomeViewTest {

    lateinit var view: HomeView
    lateinit var page: HomePage

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 640.0
        stage.height = 480.0
        val app = App(HomeView::class)
        FxToolkit.setupApplication {
            app
        }
        view = stage.uiComponent()!!
        val injector = Guice.createInjector(PageModule(arrayOf(view.root)))
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
    fun itShouldHaveEvents() {
        page.clickEventsNav()

        Assertions.assertThat(view.listMenuNav.activeItemIndex).isEqualTo(0)
    }

    @Test
    fun itShouldHaveSeasons() {
        page.clickSeasonsNav()

        Assertions.assertThat(view.listMenuNav.activeItemIndex).isEqualTo(1)
    }
}