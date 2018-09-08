package org.coner.worker.page

import javafx.scene.Node
import org.testfx.api.FxRobot
import tornadofx.*
import javax.inject.Inject

class EstablishConnectionPage @Inject constructor(
        val robot: FxRobot,
        val customPage: ConerCoreServiceConnectionDetailsPage,
        val easyModePage: EasyModeConnectionPage,
        val listMenuNavigationPage: ListMenuNavigationPage
) {
    val root: Node = robot.lookup("#establish_connection").query()
    val easyModeNav: ListMenuItem = listMenuNavigationPage.listItem(0)
    val customNav: ListMenuItem = listMenuNavigationPage.listItem(1)

    fun clickEasyModeNav() {
        robot.clickOn(easyModeNav)
    }

    fun clickCustomConnectionNav() {
        robot.clickOn(customNav)
    }
}