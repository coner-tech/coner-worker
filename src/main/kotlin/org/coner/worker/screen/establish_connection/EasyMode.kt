package org.coner.worker.screen.establish_connection

import javafx.scene.control.Alert
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*
import java.io.File

class EasyModeConnectionController : Controller() {
    val model by inject<EasyModeConnectionModel>()
    val coreProcess by di<ConerCoreProcess>()
    val coreServiceConnectionDetailsController by inject<CustomConnectionController>()
    val connectionPreferencesModel by inject<ConnectionPreferencesModel>()

    fun testSettings(spec: AttemptCustomConerCoreConnection) {
        if (coreProcess.started) coreProcess.stop()
        val settings = ConerCoreProcess.Settings(model.pathToJar.value, model.pathToConfig.value)
        coreProcess.configure(settings)
        coreProcess.start()
        coreServiceConnectionDetailsController.connect(spec)
    }

    fun onConnectSuccess(spec: AttemptCustomConerCoreConnection) {
        connectionPreferencesModel.item = ConnectionPreferences().apply {
            mode = ConnectionPreferences.Mode.Easy
            conerCoreServiceUrl = spec.applicationUri.toString()
            conerCoreAdminUrl = spec.adminUri.toString()
            // TODO: paths
        }
    }

    fun onConnectFail(spec: AttemptCustomConerCoreConnection) {
        // TODO
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
    val serviceConnectionModel by inject<ServiceConnectionModel>()
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
                enableWhen(model.valid.and(serviceConnectionModel.valid))
                action {
                    val spec = AttemptCustomConerCoreConnection(
                            applicationUri = serviceConnectionModel.applicationBaseUrl.value!!,
                            adminUri = serviceConnectionModel.adminBaseUrl.value!!
                    )
                    runAsyncWithProgress {
                        controller.testSettings(spec)
                    } success {
                        controller.onConnectSuccess(spec)
                    } fail {
                        alert(Alert.AlertType.ERROR, "Failed to connect", it.stackTrace.joinToString("\n"))
                        controller.onConnectFail(spec)
                    }
                }
            }
        }
    }

    init {
        title = messages["title"]
    }
}