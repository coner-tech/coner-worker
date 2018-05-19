package org.coner.worker.screen.establish_connection

import javafx.scene.control.Alert
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*
import java.io.File
import java.net.URI

class EasyModeConnectionController : Controller() {
    val model by inject<EasyModeConnectionModel>()
    val coreProcess by di<ConerCoreProcess>()
    val coreServiceConnectionDetailsController by inject<CustomConnectionController>()

    fun testSettings(spec: AttemptCustomConerCoreConnection) {
        if (coreProcess.started) coreProcess.stop()
        val settings = ConerCoreProcess.Settings(model.pathToJar.value, model.pathToConfig.value)
        coreProcess.configure(settings)
        coreProcess.start()
        coreServiceConnectionDetailsController.connect(spec)
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
    val connectionPreferencesModel by inject<ConnectionPreferencesModel>()
    override val root = form {
        fieldset(messages["coner_core"]) {
            field(messages["path_to_jar"]) {
                hbox(spacing = 10) {
                    textfield(model.pathToJar) {
                        required()
                        validator {
                            if (it == null) return@validator null
                            if (!File(it).exists()) error(messages["file_not_exist"]) else null
                        }
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
                        required()
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
                enableWhen(model.valid)
                action {
                    val spec = AttemptCustomConerCoreConnection(
                            applicationUri = URI(connectionPreferencesModel.conerCoreServiceUrl),
                            adminUri = URI(connectionPreferencesModel.conerCoreAdminUrl)
                    )
                    runAsyncWithProgress {
                        controller.testSettings(spec)
                    } success {
                        connectionPreferencesModel.mode = ConnectionPreferencesModel.Mode.Easy
                        connectionPreferencesModel.save()
                        // TODO: paths
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