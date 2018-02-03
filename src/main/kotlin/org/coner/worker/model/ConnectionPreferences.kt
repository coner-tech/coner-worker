package org.coner.worker.model

import javafx.beans.property.SimpleObjectProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.EqualsExclude
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.HashCodeExclude
import tornadofx.*
import java.net.URI
import javax.json.JsonObject

class ConnectionPreferences : JsonModel {

    class Keys { companion object {
        val ROOT = "connectionPreferences"
        val METHOD = "method"
        val CUSTOM_CONNECTION = "customConnection"
    } }

    class Default { companion object {
        val METHOD = Method.CUSTOM
        val CUSTOM_CONNECTION = CustomConnection.Default.model()

        fun model(): ConnectionPreferences {
            val default = ConnectionPreferences()
            default.method = METHOD
            default.customConnection = CUSTOM_CONNECTION
            return default
        }
    } }

    @EqualsExclude @HashCodeExclude
    val methodProperty = SimpleObjectProperty<Method>()
    var method by methodProperty
    @EqualsExclude @HashCodeExclude
    val customConnectionProperty = SimpleObjectProperty<CustomConnection>()
    var customConnection by customConnectionProperty

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add(Keys.METHOD, method?.name)
            add(Keys.CUSTOM_CONNECTION, customConnection?.toJSON())
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            method = Method.valueOf(string(Keys.METHOD) ?: "")
            customConnection = if (containsKey(Keys.CUSTOM_CONNECTION)) {
                getJsonObject(Keys.CUSTOM_CONNECTION).toModel()
            } else null
        }
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    enum class Method {
        CUSTOM
    }

    class CustomConnection : JsonModel {

        class Keys { companion object {
            val CONER_CORE_SERVICE_URI = "conerCoreServiceUri"
            val CONER_CORE_ADMIN_URI = "conerCoreAdminUri"
        } }

        class Default { companion object {
            val CONER_CORE_SERVICE_URI = URI("http://localhost:8080")
            val CONER_CORE_ADMIN_URI = URI("http://localhost:8081")

            fun model(): CustomConnection {
                val default = CustomConnection()
                default.conerCoreAdminUri = CONER_CORE_ADMIN_URI
                default.conerCoreServiceUri = CONER_CORE_SERVICE_URI
                return default
            }
        } }

        @EqualsExclude @HashCodeExclude
        val conerCoreServiceUriProperty = SimpleObjectProperty<URI>()
        var conerCoreServiceUri by conerCoreServiceUriProperty
        @EqualsExclude @HashCodeExclude
        val conerCoreAdminUriProperty = SimpleObjectProperty<URI>()
        var conerCoreAdminUri by conerCoreAdminUriProperty

        override fun toJSON(json: JsonBuilder) {
            with(json) {
                add(Keys.CONER_CORE_SERVICE_URI, conerCoreServiceUri?.toString())
                add(Keys.CONER_CORE_ADMIN_URI, conerCoreAdminUri?.toString())
            }
        }

        override fun updateModel(json: JsonObject) {
            with(json) {
                conerCoreServiceUri = URI(string(Keys.CONER_CORE_SERVICE_URI))
                conerCoreAdminUri = URI(string(Keys.CONER_CORE_ADMIN_URI))
            }
        }

        override fun hashCode(): Int {
            return HashCodeBuilder.reflectionHashCode(this)
        }

        override fun equals(other: Any?): Boolean {
            return EqualsBuilder.reflectionEquals(this, other)
        }
    }
}