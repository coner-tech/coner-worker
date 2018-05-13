package org.coner.worker.screen

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.TabPane
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import javafx.util.converter.IntegerStringConverter
import org.coner.core.client.ApiClient
import org.coner.core.client.ApiException
import org.coner.core.client.api.EventsApi
import org.coner.worker.WorkerStylesheet
import org.coner.worker.model.ConnectionPreferences
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*
import java.net.URI

class EstablishConnectionView : View() {

    override val root = vbox {
        id = "establish_connection"
        label(titleProperty) {
            id = "title"
            addClass(WorkerStylesheet.h1)
        }
        tabpane {
            id = "tabs"
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab(find<ConerEasyModeConnectionDetailsView>()) {

            }
            tab(find(ConerCoreServiceConnectionDetailsView::class)) {
                id = "coner-core-service-connection-details-tab"
            }
        }
    }

    init {
        title = messages["title"]
    }
}

class ConerEasyModeConnectionDetailsView : View() {
    val model by inject<ConerEasyModeConnectionDetailsModel>()
    val controller by inject<ConerEasyModeConnectionDetailsController>()
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
                    runAsyncWithProgress {
                        controller.startCoreProcess()
                    }
                }
            }
        }
    }

    init {
        title = messages["title"]
    }
}

class ConerEasyModeConnectionDetailsModel : ItemViewModel<ConerCoreProcess.Settings>() {
    val pathToJar = bind(ConerCoreProcess.Settings::pathToJar)
    val pathToConfig = bind(ConerCoreProcess.Settings::pathToConfig)
}

class ConerEasyModeConnectionDetailsController : Controller() {
    val model by inject<ConerEasyModeConnectionDetailsModel>()
    val coreProcess by di<ConerCoreProcess>()
}

class AttemptCustomConerCoreConnection(val applicationUri: URI, val adminUri: URI) : FXEvent()

class ConerCoreServiceConnectionDetailsView : View() {
    val model: ServiceConnectionModel by inject()
    val controller: ConerCoreServiceConnectionDetailsController by inject()

    override val root = form {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        fieldset("Coner Core", labelPosition = Orientation.VERTICAL) {
            hbox {
                spacing = 8.0
                field(messages["field_protocol"]) {
                    choicebox(model.protocol, listOf("http", "https")) {
                        id = "protocol"
                    }
                }
                field(messages["field_host"]) {
                    textfield(model.host) {
                        id = "host"
                        required()
                        stripWhitespace()
                    }
                }
                field(messages["field_application_port"]) {
                    textfield(model.applicationPort) {
                        id = "application_port"
                        required()
                        textFormatter = TextFormatter(IntegerStringConverter(), model.item.applicationPort)
                        stripNonInteger()
                        filterInput { it.controlNewText.isInt() }
                    }
                }
                field(messages["field_admin_port"]) {
                    textfield(model.adminPort) {
                        id = "admin_port"
                        required()
                        textFormatter = TextFormatter(IntegerStringConverter(), model.item.adminPort)
                        stripNonInteger()
                        filterInput { it.controlNewText.isInt() }
                    }
                }
            }
        }
        buttonbar {
            button(messages["button_connect"], ButtonBar.ButtonData.OK_DONE) {
                id = "connect"
                enableWhen { model.valid }
                action {
                    val spec = AttemptCustomConerCoreConnection(
                            applicationUri = model.applicationBaseUrl.get()!!,
                            adminUri = model.adminBaseUrl.get()!!
                    )
                    runAsyncWithProgress {
                        controller.connect(spec)
                    } success {
                        controller.onConnectSuccess(spec)
                        alert(Alert.AlertType.INFORMATION, "Connected")
                    } fail {
                        controller.onConnectFail(spec)
                        alert(Alert.AlertType.ERROR, "Failed to connect")
                    }
                }
            }
        }
    }

    init {
        title = messages["title"]
    }
}

class ConerCoreServiceConnectionDetailsController : Controller() {

    fun connect(attempt: AttemptCustomConerCoreConnection) {
        // request health
        val coreAdminApi = Rest()
        coreAdminApi.baseURI = attempt.adminUri.toString()
        val healthResponse = coreAdminApi.get("/healthcheck")
        try {
            if (!healthResponse.ok()) {
                val message = "Something went wrong. ${healthResponse.statusCode} ${healthResponse.reason}"
                throw ApiException(message)
            }
        } finally {
            healthResponse.consume()
        }

        // request events
        val apiClient = ApiClient()
        apiClient.basePath = attempt.applicationUri.toString()
        val eventsApi = EventsApi(apiClient)
        eventsApi.events
    }

    fun onConnectSuccess(spec: AttemptCustomConerCoreConnection) {
        saveConfig(spec)
    }

    fun onConnectFail(spec: AttemptCustomConerCoreConnection) {
        // no-op
    }

    private fun saveConfig(spec: AttemptCustomConerCoreConnection) {
        val connectionPreferences = app.config.jsonModel(ConnectionPreferences.Keys.ROOT) ?: ConnectionPreferences()
        connectionPreferences.method = ConnectionPreferences.Method.CUSTOM
        connectionPreferences.customConnection = ConnectionPreferences.CustomConnection()
        connectionPreferences.customConnection?.conerCoreAdminUri = spec.adminUri
        connectionPreferences.customConnection?.conerCoreServiceUri = spec.applicationUri
        app.config.set(ConnectionPreferences.Keys.ROOT to connectionPreferences)
        app.config.save()
    }
}

class ServiceConnectionModel : ItemViewModel<ServiceConnection>(initialValue = ServiceConnection()) {
    val protocol = bind(ServiceConnection::protocolProperty)
    val host = bind(ServiceConnection::hostProperty)
    val applicationPort = bind(ServiceConnection::applicationPortProperty)
    val adminPort = bind(ServiceConnection::adminPortProperty)
    val applicationBaseUrl = objectBinding(protocol, host, applicationPort) {
        URI("${protocol.value}://${host.value}:${applicationPort.value}")
    }
    val adminBaseUrl = objectBinding(protocol, host, adminPort) {
        URI("${protocol.value}://${host.value}:${adminPort.value}")
    }
}

class ServiceConnection {
    val protocolProperty = SimpleStringProperty(this, "protocol", "http")
    var protocol by protocolProperty
    val hostProperty = SimpleStringProperty(this, "host", "localhost")
    var host by hostProperty
    val applicationPortProperty = SimpleIntegerProperty(this, "applicationPort", 8080)
    var applicationPort by applicationPortProperty
    val adminPortProperty = SimpleIntegerProperty(this, "adminPort", 8081)
    var adminPort by adminPortProperty
}
