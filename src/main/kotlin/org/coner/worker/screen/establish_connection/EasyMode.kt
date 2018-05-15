package org.coner.worker.screen.establish_connection

import javafx.scene.control.Alert
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*
import java.net.URI

class EasyModeConnectionController : Controller() {
    val model by inject<EasyModeConnectionModel>()
    val coreProcess by di<ConerCoreProcess>()

    fun testSettings() {
        if (coreProcess.started) coreProcess.stop()
        val settings = ConerCoreProcess.Settings(model.pathToJar.value, model.pathToConfig.value)
        coreProcess.configure(settings)
        coreProcess.start().blockingAwait()
    }
}

class EasyModeConnectionModel : ItemViewModel<ConerCoreProcess.Settings>() {
    val pathToJar = bind(ConerCoreProcess.Settings::pathToJar)
    val pathToConfig = bind(ConerCoreProcess.Settings::pathToConfig)
}

class EasyModeConnectionView : View() {
    val model by inject<EasyModeConnectionModel>()
    val controller by inject<EasyModeConnectionController>()
    val coreServiceConnectionDetailsController by inject<CustomConnectionController>()
    override val root = form {
        fieldset(messages["coner_core"]) {
            field(messages["path_to_jar"]) {
                hbox(spacing = 10) {
                    textfield(model.pathToJar) {
                        hgrow = Priority.ALWAYS
                    }
                    button(messages["select"]) {
                        action {
                            val filters = arrayOf(
                                    FileChooser.ExtensionFilter(messages["filter_jar_description"], "*.jar")
                            )
                            val file = chooseFile(messages["path_to_jar"], filters).firstOrNull()
                            model.pathToJar.value = file?.toString()
                        }
                    }
                }
            }
            field(messages["path_to_config"]) {
                hbox(spacing = 10) {
                    textfield(model.pathToConfig) {
                        hgrow = Priority.ALWAYS
                    }
                    button(messages["select"]) {
                        action {
                            val file = chooseFile(title = messages["path_to_config"], filters = emptyArray()).firstOrNull()
                            model.pathToConfig.value = file?.toString()
                        }
                    }
                }
            }
            button(messages["connect"]) {
                action {
                    val spec = AttemptCustomConerCoreConnection(
                            applicationUri = URI("http://localhost:8080"),
                            adminUri = URI("http://localhost:8081")
                    )
                    runAsyncWithProgress {
                        controller.testSettings()
                        coreServiceConnectionDetailsController.connect(spec)
                    } success {
                        coreServiceConnectionDetailsController.onConnectSuccess(spec)
                    } fail {
                        alert(Alert.AlertType.ERROR, "Failed to connect", it.stackTrace.joinToString("\n"))
                        coreServiceConnectionDetailsController.onConnectFail(spec)
                    }
                }
            }
        }
    }

    init {
        title = messages["title"]
    }
}