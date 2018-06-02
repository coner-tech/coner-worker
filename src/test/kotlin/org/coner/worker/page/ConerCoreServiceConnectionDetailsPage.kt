package org.coner.worker.page

import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.coner.worker.screen.establish_connection.CustomConnectionView
import org.testfx.api.FxRobot
import org.testfx.matcher.control.TextMatchers
import java.net.URI

class ConerCoreServiceConnectionDetailsPage(val robot: FxRobot, val view: CustomConnectionView) {

    private val page = "#custom_connection #coner_core"

    val protocol: ChoiceBox<String> = robot.lookup("$page #protocol").query()
    val applicationPort: TextField = robot.lookup("$page #application_port").query()
    val adminPort: TextField = robot.lookup("$page #admin_port").query()
    val host: TextField = robot.lookup("$page #host").query()
    val connect: Button = robot.lookup("$page #connect").query()
    val realisticValues = RealisticValues(
            applicationUri = URI("http://localhost:8080"),
            adminUri = URI("http://localhost:8081")
    )

    fun fillRealisticValues() {
        with(realisticValues) {
            setProtocol(applicationUri.scheme)
            setHost(applicationUri.host)
            setApplicationPort(applicationUri.port.toString())
            setAdminPort(adminUri.port.toString())
        }
    }

    fun setProtocol(text: String) {
        robot.clickOn(protocol)
        robot.clickOn(TextMatchers.hasText(text))
    }

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

    data class RealisticValues(val applicationUri: URI, val adminUri: URI)
}