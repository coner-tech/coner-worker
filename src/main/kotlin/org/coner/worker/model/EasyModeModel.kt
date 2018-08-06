package org.coner.worker.model

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class EasyModeModel : ViewModel() {
    val startedProperty = SimpleBooleanProperty()
    var started by startedProperty

}