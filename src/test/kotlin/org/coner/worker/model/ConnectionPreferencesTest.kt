package org.coner.worker.model

import org.junit.Before
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.io.StringReader
import javax.json.Json
import kotlin.test.assertEquals

class ConnectionPreferencesTest {

    lateinit var connectionPreferences: ConnectionPreferences

    val defaultAsJson = """
{
   "method":"CUSTOM",
   "customConnection": ${CustomConnectionTest.defaultAsJson}
}
"""

    @Before
    fun before() {
        connectionPreferences = ConnectionPreferences()
    }

    @Test
    fun itShouldConvertToJson() {
        connectionPreferences = ConnectionPreferences.Default.model()

        val actual = connectionPreferences.toJSON().toString()

        val expected = defaultAsJson
        JSONAssert.assertEquals(expected, actual, false)
    }

    @Test
    fun itShouldUpdateFromJson() {
        val input = Json.createReader(StringReader(defaultAsJson)).readObject()
        val expected = ConnectionPreferences.Default.model()

        connectionPreferences.updateModel(input)

        assertEquals(expected, connectionPreferences)
    }
}

class CustomConnectionTest {
    lateinit var customConnection: ConnectionPreferences.CustomConnection

    companion object {
        val defaultAsJson = """
{
   "conerCoreServiceUri": "http://localhost:8080",
   "conerCoreAdminUri": "http://localhost:8081"
}
"""
    }

    @Before
    fun before() {
        customConnection = ConnectionPreferences.CustomConnection()
    }

    @Test
    fun itShouldConvertToJson() {
        customConnection = ConnectionPreferences.CustomConnection.Default.model()

        val actual = customConnection.toJSON().toString()

        val expected = defaultAsJson
        JSONAssert.assertEquals(expected, actual, false)
    }

    @Test
    fun itShouldUpdateFromJson() {
        val input = Json.createReader(StringReader(defaultAsJson)).readObject()
        val expected = ConnectionPreferences.CustomConnection.Default.model()

        customConnection.updateModel(input)

        assertEquals(expected, customConnection)
    }

}