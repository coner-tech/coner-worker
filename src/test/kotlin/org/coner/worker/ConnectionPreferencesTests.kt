package org.coner.worker

import org.junit.Before
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.io.StringReader
import java.net.URI
import javax.json.Json
import kotlin.test.assertEquals

class ConnectionPreferencesModelTest {

    lateinit var connectionPreferences: ConnectionPreferencesModel

    val defaultAsJson = """
{
   "mode":"Easy",
   "value": ${ConnectionPreferencesModelEasyModeTest.defaultAsJson}
}
"""

    @Before
    fun before() {
        connectionPreferences = ConnectionPreferencesModel()
    }

    @Test
    fun itShouldConvertToJson() {
        connectionPreferences = ConnectionPreferencesModel().apply {
            value = ConnectionPreferencesModel.Mode.Easy.DEFAULT
        }

        val actual = connectionPreferences.toJSON().toString()

        val expected = defaultAsJson
        JSONAssert.assertEquals(expected, actual, false)
    }

    @Test
    fun itShouldUpdateFromJson() {
        val input = Json.createReader(StringReader(defaultAsJson)).readObject()
        val expected = ConnectionPreferencesModel().apply {
            value = ConnectionPreferencesModel.Mode.Easy.DEFAULT
        }

        connectionPreferences.updateModel(input)

        assertEquals(expected, connectionPreferences)
    }
}

class ConnectionPreferencesModelEasyModeTest {
    lateinit var easyMode: ConnectionPreferencesModel.Mode.Easy

    companion object {
        val defaultAsJson = """
{

}
"""
    }

    @Before
    fun before() {
        easyMode = ConnectionPreferencesModel.Mode.Easy()
    }

    @Test
    fun itShouldConvertToJson() {
        easyMode = ConnectionPreferencesModel.Mode.Easy.DEFAULT

        val actual = easyMode.toJSON().toString()

        val expected = defaultAsJson
        JSONAssert.assertEquals(expected, actual, false)
    }

    @Test
    fun itShouldUpdateFromJson() {
        val input = Json.createReader(StringReader(defaultAsJson)).readObject()
        val expected = ConnectionPreferencesModel.Mode.Easy.DEFAULT

        easyMode.updateModel(input)

        assertEquals(expected, easyMode)
    }
}

class ConnectionPreferencesModelCustomModeTest {
    lateinit var customConnection: ConnectionPreferencesModel.Mode.Custom

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
        customConnection = ConnectionPreferencesModel.Mode.Custom()
    }

    @Test
    fun itShouldConvertToJson() {
        customConnection = ConnectionPreferencesModel.Mode.Custom.DEFAULT

        val actual = customConnection.toJSON().toString()

        val expected = defaultAsJson
        JSONAssert.assertEquals(expected, actual, false)
    }

    @Test
    fun itShouldUpdateFromJson() {
        val input = Json.createReader(StringReader(defaultAsJson)).readObject()
        val expected = ConnectionPreferencesModel.Mode.Custom.DEFAULT

        customConnection.updateModel(input)

        assertEquals(expected, customConnection)
    }

    @Test
    fun itShouldEqualsCorrectly() {
        val a = ConnectionPreferencesModel.Mode.Custom.DEFAULT
        var b: ConnectionPreferencesModel.Mode.Custom = ConnectionPreferencesModel.Mode.Custom.DEFAULT
        assertEquals(true, a.equals(b))

        b = b.copy()
        assertEquals(true, a.equals(b))

        b = ConnectionPreferencesModel.Mode.Custom().apply {
            conerCoreServiceUri = URI("http://foo.bar:1233")
            conerCoreAdminUri = URI("http://foo.bar:1234")
        }
        assertEquals(false, a.equals(b))
    }

}