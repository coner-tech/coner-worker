package org.coner.worker.page

import javafx.scene.control.Button
import javafx.scene.control.TextField
import org.testfx.api.FxRobot
import tornadofx.*

class EasyModeConnectionPage(val robot: FxRobot) {

    private val page = "#easy_mode"

    val pathToJar: TextField = robot.lookup("$page #path_to_jar").query()
    val pathToConfig: TextField = robot.lookup("$page #path_to_config").query()
    val connect: Button = robot.lookup("$page #connect").query()

    val realisticValues = RealisticValues(
            jarName = "coner-core-service.jar",
            configName = "test.yml"
    )

    fun fillValues(jar: String, config: String) {
        setJar(jar)
        setConfig(config)
    }

    fun clearJar() {
        FX.runAndWait { pathToJar.text = null }
    }

    fun setJar(text: String) {
        clearJar()
        robot.clickOn(pathToJar)
        robot.write(text)
    }

    fun getJar() = pathToJar.text

    fun clearConfig() {
        FX.runAndWait { pathToConfig.text = null }
    }

    fun setConfig(text: String) {
        clearConfig()
        robot.clickOn(pathToConfig)
        robot.write(text)
    }

    fun getConfig() = pathToConfig.text

    data class RealisticValues(val jarName: String, val configName: String)
}