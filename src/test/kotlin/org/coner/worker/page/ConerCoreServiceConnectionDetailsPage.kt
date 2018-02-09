package org.coner.worker.page

import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.coner.worker.screen.ConerCoreServiceConnectionDetailsView
import org.testfx.api.FxRobot

class ConerCoreServiceConnectionDetailsPage(val robot: FxRobot, val view: ConerCoreServiceConnectionDetailsView) {

    val protocol: ChoiceBox<String> = robot.lookup("#protocol").query()
    val applicationPort: TextField = robot.lookup("#application_port").query()
    val adminPort: TextField = robot.lookup("#admin_port").query()
    val host: TextField = robot.lookup("#host").query()
    val connect: Button = robot.lookup("#connect").query()

    fun clearHost() {
        robot.doubleClickOn(host)
        robot.type(KeyCode.BACK_SPACE)
    }

    fun setHost(text: String) {
        clearHost()
        robot.write(text)
    }

    fun clearApplicationPort() {
        robot.doubleClickOn(applicationPort)
        robot.type(KeyCode.BACK_SPACE)
    }

    fun setApplicationPort(text: String) {
        clearApplicationPort()
        robot.write(text)
    }

    fun clearAdminPort() {
        robot.doubleClickOn(adminPort)
        robot.type(KeyCode.BACK_SPACE)
    }

    fun setAdminPort(text: String) {
        clearAdminPort()
        robot.write(text)
    }

    fun connect() {
        robot.clickOn(connect)
    }
}