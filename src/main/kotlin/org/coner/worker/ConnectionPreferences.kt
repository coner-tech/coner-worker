package org.coner.worker

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ConnectionPreferencesController : Controller() {

    val model by inject<ConnectionPreferencesModel>()

    init {
        model.item = loadFromConfig()
        model.itemProperty.onChange {
            save()
        }
    }

    private fun loadFromConfig(): ConnectionPreferences {
        with(config) {
            return ConnectionPreferences().apply {
                saved = boolean("saved") ?: false
                mode = when (string("mode")) {
                    is String -> ConnectionPreferences.Mode.valueOf(string("mode"))
                    else -> null
                }

                conerCoreServiceUrl = string("conerCoreServiceUrl") ?: "http://localhost:8080"
                conerCoreAdminUrl = string("conerCoreAdminUrl") ?: "http://localhost:8081"
            }
        }
    }

    private fun save() {
        model.commit()
        val item = model.item
        with(config) {
            clear()
            set("mode" to item.mode.toString())
            set("conerCoreServiceUrl" to item.conerCoreServiceUrl)
            set("conerCoreAdminUrl" to item.conerCoreAdminUrl)
            set("saved" to true)
            save()
        }
        item.saved = true
    }
}

class ConnectionPreferencesModel : ItemViewModel<ConnectionPreferences>() {
    val saved = bind(ConnectionPreferences::savedProperty)
    val mode = bind(ConnectionPreferences::modeProperty)
    val conerCoreServiceUrl = bind(ConnectionPreferences::conerCoreServiceUrlProperty)
    val conerCoreAdminUrl = bind(ConnectionPreferences::conerCoreAdminUrlProperty)
}

class ConnectionPreferences {
    val savedProperty = SimpleBooleanProperty(this, "saved")
    var saved by savedProperty
    val modeProperty = SimpleObjectProperty<Mode?>(this, "mode")
    var mode by modeProperty
    val conerCoreServiceUrlProperty = SimpleStringProperty(this, "conerCoreServiceUrl")
    var conerCoreServiceUrl by conerCoreServiceUrlProperty
    val conerCoreAdminUrlProperty = SimpleStringProperty(this, "conerCoreAdminUrl")
    var conerCoreAdminUrl by conerCoreAdminUrlProperty

    enum class Mode {
        Easy,
        Custom
    }
}