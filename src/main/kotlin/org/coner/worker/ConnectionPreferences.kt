package org.coner.worker

import javafx.beans.property.SimpleObjectProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import tornadofx.*
import java.net.URI
import javax.json.JsonObject

class ConnectionPreferencesController : Controller() {

    val modeProperty = SimpleObjectProperty<ConnectionModePreference>()
    var mode by modeProperty

    init {
        load()
        modeProperty.onChange { save(it) }
    }

    private fun load() {
        mode = when (config.string("type")) {
            "easy" -> config.jsonModel<ConnectionModePreference.Easy>("mode")
            "custom" -> config.jsonModel<ConnectionModePreference.Custom>("mode")
            else -> ConnectionModePreference.None()
        }
    }

    private fun save(mode: ConnectionModePreference?) {
        val type = when (mode) {
            is ConnectionModePreference.Easy -> "easy"
            is ConnectionModePreference.Custom -> "custom"
            else -> "none"
        }
        config.set("type" to type)
        config.set("mode" to mode)
        config.save()
    }

}

sealed class ConnectionModePreference : JsonModel {
    class None : ConnectionModePreference()

    class Easy : Custom() {

        companion object {
            val DEFAULT = Easy().apply {
                conerCoreServiceUri = Custom.DEFAULT.conerCoreServiceUri
                conerCoreAdminUri = Custom.DEFAULT.conerCoreAdminUri
                // TODO: paths
            }
        }
    }

    open class Custom : ConnectionModePreference() {
        var conerCoreServiceUri by singleAssign<URI>()
        var conerCoreAdminUri by singleAssign<URI>()

        override fun toJSON(json: JsonBuilder) {
            with(json) {
                add("conerCoreServiceUri", conerCoreServiceUri.toString())
                add("conerCoreAdminUri", conerCoreAdminUri.toString())
            }
        }

        override fun updateModel(json: JsonObject) {
            with(json) {
                conerCoreServiceUri = URI(string("conerCoreServiceUri"))
                conerCoreAdminUri = URI(string("conerCoreAdminUri"))
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return EqualsBuilder()
                    .append(this.conerCoreServiceUri, (other as Custom).conerCoreServiceUri)
                    .append(this.conerCoreAdminUri, other.conerCoreAdminUri)
                    .build()
        }

        override fun hashCode(): Int {
            return HashCodeBuilder()
                    .append(conerCoreServiceUri)
                    .append(conerCoreAdminUri)
                    .build()
        }

        companion object {
            val DEFAULT = Custom().apply {
                conerCoreServiceUri = URI("http://localhost:8080")
                conerCoreAdminUri = URI("http://localhost:8081")
            }
        }


    }
}