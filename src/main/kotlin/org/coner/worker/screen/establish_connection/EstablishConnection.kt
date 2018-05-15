package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TabPane
import org.coner.worker.WorkerStylesheet
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