package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.TextFormatter
import javafx.scene.control.TextInputControl
import javafx.scene.layout.Priority
import javafx.util.converter.NumberStringConverter
import org.coner.core.client.ApiClient
import org.coner.core.client.ApiException
import org.coner.core.client.api.EventsApi
import org.coner.worker.ConnectionPreferences
import tornadofx.*
import java.net.URI

class CustomConnectionController : Controller(), SpecificEstablishConnectionController {
    val model: CustomConnectionModel by inject()
    val view: CustomConnectionView by inject()

    init {
        model.connectionPreferencesProperty.onChange {
            model.updateFromConnectionPreferences()
        }
    }

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
        model.connectionPreferences = ConnectionPreferences(
                saved = false,
                mode = ConnectionPreferences.Mode.CUSTOM,
                conerCoreServiceUrl = spec.applicationUri,
                conerCoreAdminUrl = spec.adminUri
        )
    }

    fun onConnectFail(spec: AttemptCustomConerCoreConnection) {
        // no-op
    }

    override fun offer(connectionPreferences: ConnectionPreferences): SpecificEstablishConnectionController.OfferResult {
        return if (connectionPreferences.mode == ConnectionPreferences.Mode.CUSTOM) {
            model.connectionPreferences = connectionPreferences
            SpecificEstablishConnectionController.OfferResult.Claimed()
        } else {
            SpecificEstablishConnectionController.OfferResult.Ignored()
        }
    }

    override fun connect(connectionPreferences: ConnectionPreferences) {
        view.onClickConnectButton()
    }
}

class CustomConnectionModel : ViewModel() {
    val protocolProperty = SimpleStringProperty(this, "protocol", "http")
    var protocol by protocolProperty

    val hostProperty = SimpleStringProperty(this, "host", "localhost")
    var host by hostProperty

    val applicationPortProperty = SimpleIntegerProperty(this, "applicationPort", 8080)
    var applicationPort by applicationPortProperty

    val adminPortProperty = SimpleIntegerProperty(this, "adminPort", 8081)
    var adminPort by adminPortProperty

    val applicationBaseUrl = objectBinding(protocolProperty, hostProperty, applicationPortProperty) {
        URI("$protocol://$host:$applicationPort")
    }
    val adminBaseUrl = objectBinding(protocolProperty, hostProperty, adminPortProperty) {
        URI("$protocol://$host:$adminPort")
    }
    val connectionPreferencesProperty = SimpleObjectProperty<ConnectionPreferences>(this, "connectionPreferences")
    var connectionPreferences by connectionPreferencesProperty

    fun updateFromUris(appUri: URI, adminUri: URI) {
        protocol = appUri.scheme
        host = appUri.host
        applicationPort = appUri.port
        adminPort = adminUri.port
    }

    fun updateFromConnectionPreferences() {
        protocol = connectionPreferences.conerCoreServiceUrl.scheme
        host = connectionPreferences.conerCoreServiceUrl.host
        applicationPort = connectionPreferences.conerCoreServiceUrl.port
        adminPort = connectionPreferences.conerCoreAdminUrl.port
    }
}


class AttemptCustomConerCoreConnection(val applicationUri: URI, val adminUri: URI)

class CustomConnectionView : View() {
    val model: CustomConnectionModel by inject()
    val controller: CustomConnectionController by inject()

    private val portNumberConverter = NumberStringConverter("#####")

    override val root = form {
        id = "custom_connection"
        vgrow = Priority.ALWAYS
        fieldset("Coner Core", labelPosition = Orientation.VERTICAL) {
            id = "coner_core"
            hbox {
                spacing = 8.0
                field(messages["field_protocol"]) {
                    choicebox(model.protocolProperty, listOf("http", "https")) {
                        validator { if (it == null) error("Choose a protocol") else null }
                        id = "protocol"
                    }
                }
                field(messages["field_host"]) {
                    textfield(model.hostProperty) {
                        id = "host"
                        required()
                        stripWhitespace()
                    }
                }
                field(messages["field_application_port"]) {
                    textfield(model.applicationPortProperty) {
                        id = "application_port"
                        required()
                        textFormatter = TextFormatter(portNumberConverter, model.applicationPort)
                        stripNonInteger()
                        requireValidPortNumber()
                    }
                }
                field(messages["field_admin_port"]) {
                    textfield(model.adminPortProperty) {
                        id = "admin_port"
                        required()
                        textFormatter = TextFormatter(portNumberConverter, model.adminPort)
                        stripNonInteger()
                        requireValidPortNumber()
                    }
                }
            }
            buttonbar {
                button(messages["button_connect"], ButtonBar.ButtonData.OK_DONE) {
                    id = "connect"
                    isDefaultButton = true
                    enableWhen { model.valid }
                    action { onClickConnectButton() }
                }
            }
        }
    }

    init {
        title = messages["title"]
    }

    override fun onDock() {
        super.onDock()
    }

    fun onClickConnectButton() {
        val spec = AttemptCustomConerCoreConnection(
                applicationUri = model.applicationBaseUrl.get()!!,
                adminUri = model.adminBaseUrl.get()!!
        )
        runAsync {
            controller.connect(spec)
        } success {
            controller.onConnectSuccess(spec)
        } fail {
            controller.onConnectFail(spec)
            alert(Alert.AlertType.ERROR, "Failed to connect")
        }
    }
}

private fun TextInputControl.requireValidPortNumber(trigger: ValidationTrigger = ValidationTrigger.OnChange(), message: String? = viewModelBundle["required"]) {
    val portNumberConverter = NumberStringConverter("#####")
    return validator(trigger) {
        if (it?.isBlank() == true) return@validator null
        if (portNumberConverter.fromString(it)?.toInt() !in 1..65536) {
            error("Invalid port number")
        } else null
    }
}