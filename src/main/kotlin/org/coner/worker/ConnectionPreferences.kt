package org.coner.worker

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import tornadofx.*
import java.net.URI

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
        string(conerCoreServiceUrlProperty.name)?.let { conerCoreServiceUrl = URI(it) }
        string(conerCoreAdminUrlProperty.name)?.let { conerCoreAdminUrl = URI(it) }
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

class ConnectionPreferences(

) {
    constructor(
            saved: Boolean,
            mode: Mode,
            conerCoreServiceUrl: URI,
            conerCoreAdminUrl: URI
    ) : this() {
        this.saved = saved
        this.mode = mode
        this.conerCoreServiceUrl = conerCoreServiceUrl
        this.conerCoreAdminUrl = conerCoreAdminUrl
    }
    val savedProperty = SimpleBooleanProperty(this, "saved")
    var saved by savedProperty
    val modeProperty = SimpleObjectProperty<Mode?>(this, "mode")
    var mode by modeProperty
    val conerCoreServiceUrlProperty = SimpleObjectProperty<URI>(this, "conerCoreServiceUrl")
    var conerCoreServiceUrl by conerCoreServiceUrlProperty
    val conerCoreAdminUrlProperty = SimpleObjectProperty<URI>(this, "conerCoreAdminUrl")
    var conerCoreAdminUrl by conerCoreAdminUrlProperty

    enum class Mode {
        EASY,
        CUSTOM
    }

    internal val properties = arrayOf(savedProperty, modeProperty, conerCoreServiceUrlProperty, conerCoreAdminUrlProperty)
    internal val propertiesNames = properties.map { "${it.name}Property" }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other, propertiesNames)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this, propertiesNames)
    }


}

class ConnectionPreferencesModel : ItemViewModel<ConnectionPreferences>() {
    val saved = bind(ConnectionPreferences::savedProperty)
    val mode = bind(ConnectionPreferences::modeProperty)
    val conerCoreServiceUrl = bind(ConnectionPreferences::conerCoreServiceUrlProperty)
    val conerCoreAdminUrl = bind(ConnectionPreferences::conerCoreAdminUrlProperty)
}