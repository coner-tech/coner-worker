package org.coner.worker

import javafx.beans.property.SimpleObjectProperty
import org.coner.worker.util.ConfigPropertiesBoundary
import tornadofx.*
import java.net.URI

class ConnectionPreferencesController : Controller() {

    val modeProperty = SimpleObjectProperty<ConnectionModePreference>()
    var mode by modeProperty

    init {
        load()
        modeProperty.onChange { save(it) }
    }

    private fun load() {
        mode = ConnectionModePreference.read(config)
    }

    private fun save(mode: ConnectionModePreference?) {
        ConnectionModePreference.write(mode, config)
        config.save()
    }

}

sealed class ConnectionModePreference {

    companion object : ConfigPropertiesBoundary<ConnectionModePreference?> {
        override fun read(configProperties: ConfigProperties): ConnectionModePreference? {
            return when (configProperties.string("type")) {
                "easy" -> Easy.read(configProperties)
                "custom" -> Custom.read(configProperties)
                else -> None()
            }
        }

        override fun write(value: ConnectionModePreference?, configProperties: ConfigProperties) {
            when (value) {
                is Easy -> {
                    Easy.write(value, configProperties)
                    configProperties.set("type" to "easy")
                }
                is Custom -> {
                    Custom.write(value, configProperties)
                    configProperties.set("type" to "custom")
                }
                is None -> {
                    configProperties.set("type" to "none")
                }
            }
        }
    }

    class None : ConnectionModePreference()

    data class Easy(
            val conerCoreServiceUri: URI,
            val conerCoreAdminUri: URI
            // TODO: paths
    ) : ConnectionModePreference() {

        companion object : ConfigPropertiesBoundary<Easy> {
            override fun read(configProperties: ConfigProperties): Easy {
                with(configProperties) {
                    return Easy(
                            conerCoreServiceUri = URI(string("conerCoreServiceUri")),
                            conerCoreAdminUri = URI(string("conerCoreAdminUri"))
                    )
                }
            }

            override fun write(value: Easy, configProperties: ConfigProperties) {
                with(configProperties) {
                    set("conerCoreServiceUri" to value.conerCoreServiceUri)
                    set("conerCoreAdminUri" to value.conerCoreAdminUri)
                }
            }

            val DEFAULT = Easy(
                    conerCoreServiceUri = Custom.DEFAULT.conerCoreServiceUri,
                    conerCoreAdminUri = Custom.DEFAULT.conerCoreAdminUri
            )
        }
    }

    data class Custom(val conerCoreServiceUri: URI, val conerCoreAdminUri: URI) : ConnectionModePreference() {

        companion object : ConfigPropertiesBoundary<Custom> {
            override fun read(configProperties: ConfigProperties): Custom {
                with(configProperties) {
                    return Custom(
                            conerCoreServiceUri = URI(string("conerCoreServiceUri")),
                            conerCoreAdminUri = URI(string("conerCoreAdminUri"))
                    )
                }
            }

            override fun write(value: Custom, configProperties: ConfigProperties) {
                with(configProperties) {
                    set("conerCoreServiceUri" to value.conerCoreServiceUri)
                    set("conerCoreAdminUri" to value.conerCoreAdminUri)
                }
            }

            val DEFAULT = Custom(
                    conerCoreServiceUri = URI("http://localhost:8080"),
                    conerCoreAdminUri = URI("http://localhost:8081")
            )
        }
    }
}