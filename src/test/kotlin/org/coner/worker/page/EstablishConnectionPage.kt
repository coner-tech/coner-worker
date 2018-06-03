package org.coner.worker.page

import javafx.scene.Node
import javafx.scene.control.TabPane
import org.testfx.api.FxRobot

class EstablishConnectionPage(val robot: FxRobot) {
    private val page = "#establish_connection"

    val tabs: TabPane = robot.lookup("$page #tabs").query()
    val easyModeTab: Node = robot.lookup("$page #easy-mode-tab").query()
    val customTab: Node = robot.lookup("$page #custom-connection-tab").query()

    fun clickEasyModeTab() {
        robot.clickOn(easyModeTab)
    }

    fun clickCustomConnectionTab() {
        robot.clickOn(customTab)


    }

    enum class Tabs(val index: Int) {
        EasyMode(0),
        Custom(1)
    }
}