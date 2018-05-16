package org.coner.worker

import javafx.beans.property.SimpleObjectProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import tornadofx.*
import java.net.URI
import javax.json.JsonObject

class ConnectionPreferencesModel : JsonModel {

    var value by singleAssign<Mode>()

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("mode", value::class.simpleName)
            add("value", value)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            val mode = string("mode") ?: ""
            value = when(mode) {
                Mode.Easy::class.simpleName -> getJsonObject("value").toModel<Mode.Easy>()
                Mode.Custom::class.simpleName -> getJsonObject("value").toModel<Mode.Custom>()
                else -> DEFAULT.value
            }
        }
    }

    sealed class Mode : JsonModel {
        class Easy : Mode() {

            companion object {
                val DEFAULT = Easy().apply {
                    // TODO: paths
                }
            }

            override fun equals(other: Any?) = EqualsBuilder.reflectionEquals(this, other)

            override fun hashCode() = HashCodeBuilder.reflectionHashCode(this)
        }
        class Custom : Mode() {
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
                return javaClass.hashCode()
            }

            companion object {
                val DEFAULT = Custom().apply {
                    conerCoreServiceUri = URI("http://localhost:8080")
                    conerCoreAdminUri = URI("http://localhost:8081")
                }
            }


        }
    }

    companion object {
        val DEFAULT = ConnectionPreferencesModel().apply {
            value = Mode.Easy.DEFAULT
        }
    }


}

class ConnectionPreferencesController : Controller() {

    val connectionPreferencesProperty = SimpleObjectProperty<ConnectionPreferencesModel>()
    var connectionPreferences by connectionPreferencesProperty

    init {
        connectionPreferences = app.config.jsonModel("connectionPreferences")
        connectionPreferencesProperty.onChange {
            app.config.set("connectionPreferences" to it)
            app.config.save()
        }
    }
}