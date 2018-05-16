package org.coner.worker.screen.establish_connection

import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Priority
import javafx.util.converter.IntegerStringConverter
import org.coner.core.client.ApiClient
import org.coner.core.client.ApiException
import org.coner.core.client.api.EventsApi
import org.coner.worker.ConnectionPreferencesController
import org.coner.worker.ConnectionPreferencesModel
import tornadofx.*
import java.net.URI

class CustomConnectionController : Controller() {

    val connectionPreferencesController by inject<ConnectionPreferencesController>()

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
        val connectionPreferences = ConnectionPreferencesModel()
        connectionPreferences.value = ConnectionPreferencesModel.Mode.Custom().apply {
            conerCoreServiceUri = spec.applicationUri
            conerCoreAdminUri = spec.adminUri
        }
        connectionPreferencesController.connectionPreferences = connectionPreferences
    }

    fun onConnectFail(spec: AttemptCustomConerCoreConnection) {
        // no-op
    }
}


class AttemptCustomConerCoreConnection(val applicationUri: URI, val adminUri: URI)

class CustomConnectionView : View() {
    val model: ServiceConnectionModel by inject()
    val controller: CustomConnectionController by inject()

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