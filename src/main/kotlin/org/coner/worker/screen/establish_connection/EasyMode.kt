package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.controller.MavenController
import org.coner.worker.model.MavenModel
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*
import java.net.URI

class EasyModeConnectionController : Controller() {
    val model by inject<EasyModeConnectionModel>()
    val coreProcess by di<ConerCoreProcess>()
    val coreServiceConnectionDetailsController by inject<CustomConnectionController>()
    val connectionPreferencesModel by inject<ConnectionPreferencesModel>()
    val maven by inject<MavenController>()

    fun startConerCoreService(spec: AttemptCustomConerCoreConnection) {
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
        }
    }

    fun onConnectFail(spec: AttemptCustomConerCoreConnection) {
        // TODO
    }

    fun useEasyMode() {
        val conerCoreArtifact = maven.resolve(MavenModel.ArtifactKey.ConerCoreService)
        model.pathToJar.value = conerCoreArtifact.file.absolutePath
        model.pathToConfig.value = "it/config/coner-core-service.yml" // TODO: config from compiled resource
        val defaultPreferences = ConnectionPreferences()
        val attemptSpec = AttemptCustomConerCoreConnection(
                URI(defaultPreferences.conerCoreServiceUrl),
                URI(defaultPreferences.conerCoreAdminUrl)
        )
        startConerCoreService(attemptSpec)
    }
}

class EasyModeConnectionModel : ItemViewModel<ConerCoreProcess.Settings>() {
    val pathToJar = bind(ConerCoreProcess.Settings::pathToJar)
    val pathToConfig = bind(ConerCoreProcess.Settings::pathToConfig)
}

class EasyModeConnectionView : View() {
    val model by inject<EasyModeConnectionModel>()
    val controller by inject<EasyModeConnectionController>()

    val useEasyModeTaskProperty = SimpleObjectProperty<Task<Unit>>()
    var useEasyModeTask by useEasyModeTaskProperty
    val errorMessageProperty = SimpleStringProperty()
    var errorMessage by errorMessageProperty

    override val root = stackpane {
        id = "easy_mode"
        useMaxSize = true
        padding = tornadofx.insets(8)
        vbox(spacing = 8) {
            id = "easy_mode_wrapper"
            alignment = Pos.CENTER
            button(messages["use_easy_mode"]) {
                id = "use_easy_mode"
                isDefaultButton = true
                action {
                    useEasyModeTask = runAsync {
                        log.entering(this@EasyModeConnectionView::class.simpleName.toString(), "useEasyMode")
                        controller.useEasyMode()
                        log.exiting(this@EasyModeConnectionView::class.simpleName.toString(), "useEasyMode")
                    } success {
                        dialog {
                            text("Hello")
                        }
                    } fail {
                        useEasyModeTask = null
                        dialog(
                                title = messages["use_easy_mode_error_title"],
                                owner = currentWindow
                        ) {
                            textarea(it.message) {
                                isEditable = false
                                isWrapText = true
                                prefWidthProperty().bind(this@dialog.widthProperty())
                                prefHeightProperty().bind(this@dialog.heightProperty())
                            }
                        }
                    }
                }
            }
            visibleWhen { useEasyModeTaskProperty.isNull }
        }

        vbox(spacing = 8) {
            id = "easy_mode_progress"
            alignment = Pos.CENTER
            progressindicator()
            label(messages["use_easy_mode_progress_text"]) {
                isWrapText = true
            }
            visibleWhen { useEasyModeTaskProperty.isNotNull }
        }

    }

    init {
        title = messages["title"]
    }

    override fun onDock() {
        super.onDock()
        useEasyModeTask = null
    }
}