package org.coner.worker.page

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.StackPane
import org.coner.worker.util.testfx.lookupAndQuery
import org.testfx.api.FxRobot
import tornadofx.*
import javax.inject.Inject

class ListMenuNavigationPage @Inject constructor(
        val robot: FxRobot,
        vararg parentNodes: Node
) {

    val root: Parent = robot.from(*parentNodes).lookupAndQuery(".list-menu-navigation-fragment")
    val scrollPane: ScrollPane = robot.from(root).lookupAndQuery(".scroll-pane")
    val listMenu: ListMenu = robot.from(scrollPane).lookupAndQuery(".list-menu")
    val contentPane: StackPane = robot.from(root).lookupAndQuery(".content-pane")

    fun <T : Node> listItem(nth: Int): T = robot.from(listMenu).lookup(".list-item").nth(nth).query()
}