package org.coner.worker.widget

import org.coner.worker.page.ListMenuNavigationPage
import org.coner.worker.util.javafx.scene.control.scrollToChild
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions
import tornadofx.*

class ListMenuNavigationFragmentTest {

    lateinit var mainView: TestListMenuNavigationMainView
    lateinit var fragment: ListMenuNavigationFragment

    lateinit var robot: FxRobot
    lateinit var page: TestListMenuNavigationMainPage

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { TestListMenuNavigationApp() }
        mainView = stage.uiComponent()!!
        robot = FxRobot()
        val listMenuNavigationPanePage = ListMenuNavigationPage(
                robot,
                mainView.root
        )
        page = TestListMenuNavigationMainPage(listMenuNavigationPanePage)
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(mainView.app)
    }

    @Test
    fun itShouldInitToIndexZero() {
        Assertions.assertThat(page.nav[0].text).isEqualTo("0")
        Assertions.assertThat(page.numberDisplayText()).hasText("Zero")
    }

    @Test
    fun itShouldSwitchToThree() {
        Assertions.assertThat(page.nav[3].text).isEqualTo("3")
        robot.clickOn(page.nav[3])
        Thread.sleep(500)
        Assertions.assertThat(page.numberDisplayText()).hasText("Three")
    }

    @Test
    fun itShouldSwitchToNine() {
        page.listMenuNavigation.scrollPane.scrollToChild(page.nav[9])
        robot.clickOn(page.nav[9])
        Thread.sleep(500)
        Assertions.assertThat(page.numberDisplayText()).hasText("Nine")
    }

    @Test
    fun itShouldSwitchDown() {
        robot.clickOn(page.nav[9])
        Thread.sleep(500)
        robot.clickOn(page.nav[8])
        Thread.sleep(500)
        Assertions.assertThat(page.numberDisplayText()).hasText("Eight")
    }
}