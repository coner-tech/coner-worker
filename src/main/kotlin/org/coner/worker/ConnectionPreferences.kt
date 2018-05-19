package org.coner.worker

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ConnectionPreferencesModel : ViewModel() {

    val savedProperty = SimpleBooleanProperty()
    var saved by savedProperty
        private set
    val modeProperty = SimpleObjectProperty<Mode?>()
    var mode by modeProperty
    val conerCoreServiceUrlProperty = SimpleStringProperty()
    var conerCoreServiceUrl by conerCoreServiceUrlProperty
    val conerCoreAdminUrlProperty = SimpleStringProperty()
    var conerCoreAdminUrl by conerCoreAdminUrlProperty

    init {
        load()
    }

    private fun load() {
        with(config) {
            saved = boolean("saved") ?: false
            mode = when (string("mode")) {
                is String -> Mode.valueOf(string("mode"))
                else -> null
            }
            conerCoreServiceUrl = string("conerCoreServiceUrl") ?: "http://localhost:8080"
            conerCoreAdminUrl = string("conerCoreAdminUrl") ?: "http://localhost:8081"
        }
    }

    fun save() {
        with(config) {
            clear()
            set("saved" to true)
            set("mode" to mode?.toString())
            set("conerCoreServiceUrl" to conerCoreServiceUrl)
            set("conerCoreAdminUrl" to conerCoreAdminUrl)
            save()
        }
        saved = true
    }

    enum class Mode {
        Easy,
        Custom
    }

}