package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.WorkerStylesheet
import tornadofx.*
import java.net.URI

class EstablishConnectionView : View() {

    val controller: EstablishConnectionController by inject()

    override val root = vbox {
        id = "establish_connection"
        label(titleProperty) {
            id = "title"
            addClass(WorkerStylesheet.h1)
        }
        tabpane {
            id = "tabs"
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            vgrow = Priority.ALWAYS
            tab(find<EasyModeConnectionView>()) {
                id = "easy-mode-tab"
            }
            tab(find(CustomConnectionView::class)) {
                id = "custom-connection-tab"
            }
        }
    }

    init {
        title = messages["title"]
        controller.noOp()
    }
}

class EstablishConnectionController : Controller() {

    val connectionPreferencesModel: ConnectionPreferencesModel by inject()
    val conerCoreServiceConnectionModel: ServiceConnectionModel by inject()

    init {
        val appUri = URI(connectionPreferencesModel.item.conerCoreServiceUrl)
        val adminUri = URI(connectionPreferencesModel.item.conerCoreAdminUrl)
        conerCoreServiceConnectionModel.updateFromUris(appUri, adminUri)
    }

    fun noOp() {
        // no-op
        // needed to guarantee controller init
    }
}

class ServiceConnection {
    val protocolProperty = SimpleStringProperty(this, "protocol")
    var protocol by protocolProperty
    val hostProperty = SimpleStringProperty(this, "host")
    var host by hostProperty
    val applicationPortProperty = SimpleIntegerProperty(this, "applicationPort")
    var applicationPort by applicationPortProperty
    val adminPortProperty = SimpleIntegerProperty(this, "adminPort")
    var adminPort by adminPortProperty
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

    fun updateFromUris(appUri: URI, adminUri: URI) {
        protocol.value = appUri.scheme
        host.value = appUri.host
        applicationPort.value = appUri.port
        adminPort.value = adminUri.port
    }
}