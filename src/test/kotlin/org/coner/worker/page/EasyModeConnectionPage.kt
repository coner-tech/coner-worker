package org.coner.worker.page

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import org.testfx.api.FxRobot
import tornadofx.*
import javax.inject.Inject

class EasyModeConnectionPage @Inject constructor(
        val robot: FxRobot
) {

    val root: Node = robot.lookup("#easy_mode").query()
    val pathToJar: TextField = robot.from(root).lookup("#path_to_jar").query()
    val pathToConfig: TextField = robot.from(root).lookup("#path_to_config").query()
    val connect: Button = robot.from(root).lookup("#connect").query()

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