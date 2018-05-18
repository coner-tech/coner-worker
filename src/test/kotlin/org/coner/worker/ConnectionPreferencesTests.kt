package org.coner.worker

//class ConnectionPreferencesModelEasyModeTest {
//    lateinit var easyMode: ConnectionModePreference.Easy
//
//    companion object {
//        val defaultAsJson = ConnectionPreferencesModelCustomModeTest.defaultAsJson
//    }
//
//    @Before
//    fun before() {
//        easyMode = ConnectionModePreference.Easy()
//    }
//
//    @Test
//    fun itShouldConvertToJson() {
//        easyMode = ConnectionModePreference.Easy.DEFAULT
//
//        val actual = easyMode.toJSON().toString()
//
//        val expected = defaultAsJson
//        JSONAssert.assertEquals(expected, actual, false)
//    }
//
//    @Test
//    fun itShouldUpdateFromJson() {
//        val input = Json.createReader(StringReader(defaultAsJson)).readObject()
//        val expected = ConnectionModePreference.Easy.DEFAULT
//
//        easyMode.updateModel(input)
//
//        assertEquals(expected, easyMode)
//    }
//}
//
//class ConnectionPreferencesModelCustomModeTest {
//    lateinit var customConnection: ConnectionModePreference.Custom
//
//    companion object {
//        val defaultAsJson = """
//{
//   "conerCoreServiceUri": "http://localhost:8080",
//   "conerCoreAdminUri": "http://localhost:8081"
//}
//"""
//    }
//
//    @Before
//    fun before() {
//        customConnection = ConnectionModePreference.Custom()
//    }
//
//    @Test
//    fun itShouldConvertToJson() {
//        customConnection = ConnectionModePreference.Custom.DEFAULT
//
//        val actual = customConnection.toJSON().toString()
//
//        val expected = defaultAsJson
//        JSONAssert.assertEquals(expected, actual, false)
//    }
//
//    @Test
//    fun itShouldUpdateFromJson() {
//        val input = Json.createReader(StringReader(defaultAsJson)).readObject()
//        val expected = ConnectionModePreference.Custom.DEFAULT
//
//        customConnection.updateModel(input)
//
//        assertEquals(expected, customConnection)
//    }
//
//    @Test
//    fun itShouldEqualsCorrectly() {
//        val a = ConnectionModePreference.Custom.DEFAULT
//        var b: ConnectionModePreference.Custom = ConnectionModePreference.Custom.DEFAULT
//        assertEquals(true, a.equals(b))
//
//        b = b.copy()
//        assertEquals(true, a.equals(b))
//
//        b = ConnectionModePreference.Custom().apply {
//            conerCoreServiceUri = URI("http://foo.bar:1233")
//            conerCoreAdminUri = URI("http://foo.bar:1234")
//        }
//        assertEquals(false, a.equals(b))
//    }
//
//}