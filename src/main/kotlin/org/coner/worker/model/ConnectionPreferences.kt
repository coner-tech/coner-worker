package org.coner.worker.model

import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.net.URI
import javax.json.JsonObject

class ConnectionPreferences : JsonModel {

    val methodProperty = SimpleObjectProperty<Method>()
    var method by methodProperty
    val customConnectionProperty = SimpleObjectProperty<CustomConnection>()
    var customConnection by customConnectionProperty

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add(KEY_CONNECTION_PREFERENCES_METHOD, method?.name)
            add(KEY_CONNECTION_PREFERENCES_CUSTOM_CONNECTION, customConnection?.toJSON())
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            method = Method.valueOf(string(KEY_CONNECTION_PREFERENCES_METHOD) ?: "")
            customConnection = getJsonObject(KEY_CONNECTION_PREFERENCES_CUSTOM_CONNECTION).toModel()
        }
    }


    enum class Method {
        CUSTOM
    }

    class CustomConnection : JsonModel {

        val conerCoreServiceUriProperty = SimpleObjectProperty<URI>()
        var conerCoreServiceUri by conerCoreServiceUriProperty
        val conerCoreAdminUriProperty = SimpleObjectProperty<URI>()
        var conerCoreAdminUri by conerCoreAdminUriProperty

        override fun toJSON(json: JsonBuilder) {
            with(json) {
                add(KEY_CUSTOM_CONER_CORE_SERVICE_URI, conerCoreServiceUri?.toString())
                add(KEY_CUSTOM_CONER_CORE_ADMIN_URI, conerCoreAdminUri?.toString())
            }
        }

        override fun updateModel(json: JsonObject) {
            with(json) {
                conerCoreServiceUri = URI(string(KEY_CUSTOM_CONER_CORE_SERVICE_URI))
                conerCoreAdminUri = URI(string(KEY_CUSTOM_CONER_CORE_ADMIN_URI))
            }
        }
    }
}

public const val KEY_CONNECTION_PREFERENCES = "ConnectionPreferences"
private const val KEY_CONNECTION_PREFERENCES_METHOD = "method"
private const val KEY_CONNECTION_PREFERENCES_CUSTOM_CONNECTION = "customConnection"
private const val KEY_CUSTOM_CONER_CORE_SERVICE_URI = "conerCoreServiceUri"
private const val KEY_CUSTOM_CONER_CORE_ADMIN_URI = "conerCoreAdminUri"