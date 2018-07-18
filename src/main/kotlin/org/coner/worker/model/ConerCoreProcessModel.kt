package org.coner.worker.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ConerCoreProcessModel : ViewModel() {
    val jarFileProperty = SimpleStringProperty()
    var jarFile by jarFileProperty

    val configFileProperty = SimpleStringProperty()
    var configFile by configFileProperty


}