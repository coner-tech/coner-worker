package org.coner.worker.model

import tornadofx.*
import java.net.URI
import java.net.URISyntaxException
import javax.json.JsonObject

class ConnectionPreferences : JsonModel {

    var externalConerCoreServiceUri: URI? = null

    override fun toJSON(json: JsonBuilder) {
        if (externalConerCoreServiceUri != null) {
            json.add(KEY_EXTERNAL_CONER_CORE_SERVICE_URI, externalConerCoreServiceUri.toString())
        }
    }

    override fun updateModel(json: JsonObject) {
        externalConerCoreServiceUri = if (json.containsKey(KEY_EXTERNAL_CONER_CORE_SERVICE_URI)) {
            try {
                URI(json.getString(KEY_EXTERNAL_CONER_CORE_SERVICE_URI))
            } catch (e: URISyntaxException) {
                null
            }
        } else null
    }
}

private const val KEY_EXTERNAL_CONER_CORE_SERVICE_URI = "externalConerCoreServiceUri"