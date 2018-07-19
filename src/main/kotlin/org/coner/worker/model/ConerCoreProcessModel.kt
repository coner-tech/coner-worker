package org.coner.worker.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ConerCoreProcessModel : ViewModel() {
    val jarFileProperty = SimpleStringProperty()
    var jarFile by jarFileProperty

    val configFileProperty = SimpleStringProperty()
    var configFile by configFileProperty

    val serviceUrlProperty = SimpleStringProperty("http://localhost:8080")
    var serviceUrl by serviceUrlProperty

    val adminUrlProperty = SimpleStringProperty("http://localhost:8081")
    var adminUrl by adminUrlProperty

}