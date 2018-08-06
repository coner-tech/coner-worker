package org.coner.worker.page

import javafx.scene.Node
import javafx.scene.control.TabPane
import org.testfx.api.FxRobot
import javax.inject.Inject

class EstablishConnectionPage @Inject constructor(
        val robot: FxRobot,
        val customPage: ConerCoreServiceConnectionDetailsPage,
        val easyModePage: EasyModeConnectionPage
) {
    val root: Node = robot.lookup("#establish_connection").query()
    val tabs: TabPane = robot.from(root).lookup("#tabs").query()
    val easyModeTab: Node = robot.from(root).lookup("#easy-mode-tab").query()
    val customTab: Node = robot.from(root).lookup("#custom-connection-tab").query()

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