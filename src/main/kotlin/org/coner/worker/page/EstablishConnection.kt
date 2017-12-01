package org.coner.worker.page

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.ButtonBar
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Priority
import javafx.util.converter.IntegerStringConverter
import org.coner.core.client.ApiClient
import org.coner.core.client.ApiException
import org.coner.core.client.api.EventsApi
import org.coner.core.client.model.GetEventsResponse
import tornadofx.*

class ConerCoreConnectionDetailsView : View() {
    val model: EstablishConnectionModel by inject()
    val controller: EstablishConnectionController by inject()

    override val root = form {
        hgrow = Priority.ALWAYS
        fieldset(messages["fieldset_text"], labelPosition = Orientation.VERTICAL) {
            hbox {
                spacing = 8.0
                field(messages["field_protocol"]) {
                    choicebox(model.protocol, EstablishConnection.Protocol.values().toList()) {
                    }
                }
                field(messages["field_host"]) {
                    textfield(model.host).required()
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
                    runAsyncWithProgress {
                        controller.connect()
                    } ui {
                        model.commit()
                    }
                }
            }
        }
    }
}

class EstablishConnectionController : Controller() {
    val model: EstablishConnectionModel by inject()

    fun connect() {
        requestHealth()
        requestEvents()
    }

    private fun requestHealth() {
        val coreAdminApi = Rest()
        coreAdminApi.baseURI = model.adminBaseUrl.value
        val healthResponse = coreAdminApi.get("/healthcheck")
        try {
            if (healthResponse.ok()) {
                return
            } else {
                val message = "Something went wrong. ${healthResponse.statusCode} ${healthResponse.reason}"
                throw ApiException(message)
            }
        } finally {
            healthResponse.consume()
        }
    }

    private fun requestEvents(): GetEventsResponse {
        val apiClient = ApiClient()
        apiClient.basePath = model.applicationBaseUrl.value
        val eventsApi = EventsApi(apiClient)
        return eventsApi.events
    }
}

class EstablishConnectionModel : ItemViewModel<EstablishConnection>(initialValue = EstablishConnection()) {
    val protocol = bind(EstablishConnection::protocolProperty)
    val host = bind(EstablishConnection::hostProperty)
    val applicationPort = bind(EstablishConnection::applicationPortProperty)
    val adminPort = bind(EstablishConnection::adminPortProperty)
    val applicationBaseUrl = stringBinding(host, applicationPort) {
        "http://${host.value}:${applicationPort.value}"
    }
    val adminBaseUrl = stringBinding(host, adminPort) {
        "http://${host.value}:${adminPort.value}"
    }
}

class EstablishConnection {
    val protocolProperty = SimpleObjectProperty(this, "protocol", Protocol.HTTP)
    val hostProperty = SimpleStringProperty(this, "host", "localhost")
    var host by hostProperty
    val applicationPortProperty = SimpleIntegerProperty(this, "applicationPort", 8080)
    var applicationPort by applicationPortProperty
    val adminPortProperty = SimpleIntegerProperty(this, "adminPort", 8081)
    var adminPort by adminPortProperty

    enum class Protocol {
        HTTP,
        HTTPS
    }
}

