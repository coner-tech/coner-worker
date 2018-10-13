package org.coner.worker.page

import javafx.scene.Node
import org.coner.worker.util.testfx.lookupAndQuery
import org.testfx.api.FxRobot
import tornadofx.*
import javax.inject.Inject

class HomePage @Inject constructor(
        val robot: FxRobot,
        val listMenuNavigationPage: ListMenuNavigationPage
) {

    val root by lazy { robot.lookupAndQuery<Node>("#home") }
    val eventsNav by lazy { listMenuNavigationPage.listItem<ListMenuItem>(0) }
    val seasonsNav by lazy { listMenuNavigationPage.listItem<ListMenuItem>(1) }

    fun clickEventsNav() {
        robot.clickOn(eventsNav)
    }

    fun clickSeasonsNav() {
        robot.clickOn(seasonsNav)
    }
}