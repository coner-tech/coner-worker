package org.coner.worker.page

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.testfx.api.FxRobot
import org.testfx.matcher.control.TextMatchers
import java.net.URI
import javax.inject.Inject

class ConerCoreServiceConnectionDetailsPage @Inject constructor(
        val robot: FxRobot
) {

    val root by lazy { robot.lookup("#custom_connection #coner_core").query<Node>() }
    val protocol by lazy { robot.from(root).lookup("#protocol").query<ChoiceBox<String>>() }
    val applicationPort by lazy { robot.from(root).lookup("#application_port").query<TextField>() }
    val adminPort by lazy { robot.from(root).lookup("#admin_port").query<TextField>() }
    val host by lazy { robot.from(root).lookup("#host").query<TextField>() }
    val connect by lazy { robot.from(root).lookup("#connect").query<Button>() }
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