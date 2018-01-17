package org.coner.worker.page

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.TabPane
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Priority
import javafx.util.converter.IntegerStringConverter
import org.coner.core.client.ApiClient
import org.coner.core.client.ApiException
import org.coner.core.client.api.EventsApi
import org.coner.worker.WorkerStylesheet
import org.coner.worker.model.ConnectionPreferences
import org.coner.worker.model.KEY_CONNECTION_PREFERENCES
import tornadofx.*
import java.net.URI

class EstablishConnectionView : View() {

    override val root = vbox {
        label(titleProperty) {
            addClass(WorkerStylesheet.h1)
        }
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab(find(ConerCoreServiceConnectionDetailsView::class)) {
            }
        }
    }

    init {
        title = messages["title"]
    }
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
                    }
                }
                field(messages["field_host"]) {
                    textfield(model.host) {
                        required()
                        stripWhitespace()
                    }
                }
                field(messages["field_application_port"]) {
                    textfield(model.applicationPort) {
                        required()
                        textFormatter = TextFormatter(IntegerStringConverter(), model.item.applicationPort)
                        stripNonInteger()
                    }
                }
                field(messages["field_admin_port"]) {
                    textfield(model.adminPort) {
                        required()
                        textFormatter = TextFormatter(IntegerStringConverter(), model.item.adminPort)
                        stripNonInteger()
                    }
                }
            }
        }
        buttonbar {
            button(messages["button_connect"], ButtonBar.ButtonData.OK_DONE) {
                enableWhen { model.valid }
                action {
                    val spec = AttemptCustomConerCoreConnection(
                            applicationUri = model.applicationBaseUrl.get()!!,
                            adminUri = model.adminBaseUrl.get()!!
                    )
                    runAsyncWithProgress {
                        controller.connect(spec)
                    } success {
                        controller.saveConfig(spec)
                        alert(Alert.AlertType.INFORMATION, "Connected")
                    } fail {
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

    fun saveConfig(spec: AttemptCustomConerCoreConnection) {
        val connectionPreferences = app.config.jsonModel(KEY_CONNECTION_PREFERENCES) ?: ConnectionPreferences()
        connectionPreferences.method = ConnectionPreferences.Method.CUSTOM
        connectionPreferences.customConnection = ConnectionPreferences.CustomConnection()
        connectionPreferences.customConnection?.conerCoreAdminUri = spec.adminUri
        connectionPreferences.customConnection?.conerCoreServiceUri = spec.applicationUri
        app.config.set(KEY_CONNECTION_PREFERENCES to connectionPreferences)
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
