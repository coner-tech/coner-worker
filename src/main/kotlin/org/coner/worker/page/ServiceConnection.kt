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
import org.coner.core.client.model.GetEventsResponse
import org.coner.worker.WorkerStylesheet
import org.coner.worker.model.ConnectionPreferences
import tornadofx.*
import java.net.URI

class EstablishConnectionView : View() {

    val controller: EstablishConnectionController by inject()
    val conerCoreScope = ServiceScope(Service.ConerCore())

    override val root = vbox {
        label(titleProperty) {
            addClass(WorkerStylesheet.h1)
        }
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab(find(ServiceConnectionDetailsView::class, conerCoreScope)) {

            }
        }
    }

    init {
        title = messages["title"]
    }
}

class EstablishConnectionController : Controller() {

    init {
        subscribe<CustomServiceConnectionEstablished> {
            // TODO: dependency injection of connection preferences gateway
            val prefs = app.config.jsonModel<ConnectionPreferences>(ConnectionPreferences::class.simpleName!!)
            when (it.service) {
                is Service.ConerCore -> {
                    prefs!!.externalConerCoreServiceUri = it.applicationUri
                }
            }
        }
    }


}

class ServiceScope(val service: Service) : Scope()

sealed class Service(val title: String, val model: ServiceConnectionModel = ServiceConnectionModel()) {
    class ConerCore : Service("Coner Core") {

        override fun connect() {
            requestHealth()
            requestEvents()
        }

        private fun requestHealth() {
            val coreAdminApi = Rest()
            coreAdminApi.baseURI = model.adminBaseUrl.value.toString()
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
            apiClient.basePath = model.applicationBaseUrl.value.toString()
            val eventsApi = EventsApi(apiClient)
            return eventsApi.events
        }
    }

    abstract fun connect()
}

class CustomServiceConnectionEstablished(val service: Service, val applicationUri: URI, val adminUri: URI) : FXEvent()

class ServiceConnectionDetailsView : View() {
    override val scope = super.scope as ServiceScope
    val model: ServiceConnectionModel by inject()

    override val root = form {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        fieldset(scope.service.title, labelPosition = Orientation.VERTICAL) {
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
                    runAsyncWithProgress {
                        scope.service.connect()
                    } success {
                        model.commit()
                        fire(CustomServiceConnectionEstablished(
                                service = scope.service,
                                applicationUri = model.applicationBaseUrl.get()!!,
                                adminUri = model.adminBaseUrl.get()!!
                        ))
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
    val hostProperty = SimpleStringProperty(this, "host", "localhost")
    var host by hostProperty
    val applicationPortProperty = SimpleIntegerProperty(this, "applicationPort", 8080)
    var applicationPort by applicationPortProperty
    val adminPortProperty = SimpleIntegerProperty(this, "adminPort", 8081)
    var adminPort by adminPortProperty
}
