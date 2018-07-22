package org.coner.worker.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.net.URI

class ConerCoreProcessModel : ViewModel() {
    val jarFileProperty = SimpleStringProperty()
    var jarFile by jarFileProperty

    val configFileProperty = SimpleStringProperty()
    var configFile by configFileProperty

    val serviceUrlProperty = SimpleObjectProperty<URI>(URI("http://localhost:8080"))
    var serviceUrl by serviceUrlProperty

    val adminUrlProperty = SimpleObjectProperty<URI>(URI("http://localhost:8081"))
    var adminUrl by adminUrlProperty

}