package org.coner.worker

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ConnectionPreferencesController : Controller() {

    val model by inject<ConnectionPreferencesModel>()

    init {
        model.item = if (config.hasConnectionPreferences()) config.loadConnectionPreferences() else ConnectionPreferences()
        model.itemProperty.onChange {
            save()
        }
    }

    private fun save() {
        model.commit()
        val item = model.item
        config.saveConnectionPreferences(item)
        item.saved = true
    }
}

private fun ConfigProperties.hasConnectionPreferences() = ConnectionPreferences().properties.all { containsKey(it.name) }

private fun ConfigProperties.loadConnectionPreferences(): ConnectionPreferences {
    return ConnectionPreferences().apply {
        boolean(savedProperty.name)?.let { saved = it }
        string(modeProperty.name)?.let { mode = ConnectionPreferences.Mode.valueOf(it) }
        string(conerCoreServiceUrlProperty.name)?.let { conerCoreServiceUrl = it }
        string(conerCoreAdminUrlProperty.name)?.let { conerCoreAdminUrl = it }
    }
}

private fun ConfigProperties.saveConnectionPreferences(item: ConnectionPreferences) {
    clear()
    set(item.modeProperty.name to item.mode.toString())
    set(item.conerCoreServiceUrlProperty.name to item.conerCoreServiceUrl)
    set(item.conerCoreAdminUrlProperty.name to item.conerCoreAdminUrl)
    set(item.savedProperty.name to true)
    save()
}

class ConnectionPreferencesModel : ItemViewModel<ConnectionPreferences>() {
    val saved = bind(ConnectionPreferences::savedProperty)
    val mode = bind(ConnectionPreferences::modeProperty)
    val conerCoreServiceUrl = bind(ConnectionPreferences::conerCoreServiceUrlProperty)
    val conerCoreAdminUrl = bind(ConnectionPreferences::conerCoreAdminUrlProperty)
}

class ConnectionPreferences {
    val savedProperty = SimpleBooleanProperty(this, "saved", false)
    var saved by savedProperty
    val modeProperty = SimpleObjectProperty<Mode?>(this, "mode", Mode.Easy)
    var mode by modeProperty
    val conerCoreServiceUrlProperty = SimpleStringProperty(this, "conerCoreServiceUrl", "http://localhost:8080")
    var conerCoreServiceUrl by conerCoreServiceUrlProperty
    val conerCoreAdminUrlProperty = SimpleStringProperty(this, "conerCoreAdminUrl", "http://localhost:8081")
    var conerCoreAdminUrl by conerCoreAdminUrlProperty

    enum class Mode {
        Easy,
        Custom
    }

    internal val properties = arrayOf(savedProperty, modeProperty, conerCoreServiceUrlProperty, conerCoreAdminUrlProperty)
}