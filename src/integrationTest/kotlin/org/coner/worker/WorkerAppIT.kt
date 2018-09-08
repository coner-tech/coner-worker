package org.coner.worker

import org.coner.worker.page.EasyModeConnectionPage
import org.coner.worker.page.HomePage
import org.coner.worker.page.ListMenuNavigationPage
import org.coner.worker.util.testfx.lookupAndQuery
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions
import org.testfx.matcher.base.NodeMatchers.isVisible

class WorkerAppIT {

    lateinit var app: WorkerApp
    @Rule
    @JvmField
    val folder = TemporaryFolder()

    @Before
    fun before() {
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication {
            app = object : WorkerApp() {
                override val configBasePath = folder.newFolder().toPath()
            }
            app
        }
    }

    @After
    fun after() {
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

    @Test
    fun itShouldNavigateToHomeWhenEasyModeConnects() {
        val robot = FxRobot()
        val easyModePage = EasyModeConnectionPage(robot)
        easyModePage.clickUseButton()

        var matched = false
        var homePage: HomePage? = null
        while (!matched) {
            try {
                homePage = HomePage(
                        robot,
                        ListMenuNavigationPage(
                                robot,
                                robot.lookupAndQuery("#home")
                        )
                )
                matched = true
            } catch (t: Throwable) {
                Thread.sleep(160)
                continue
            }
        }
        Assertions.assertThat(homePage?.root).isVisible
        robot.closeCurrentWindow()
        robot.clickOn("OK")
    }

}