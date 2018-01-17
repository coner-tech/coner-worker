package org.coner.worker.page

import org.junit.Before
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import java.net.URI
import kotlin.test.assertEquals

class ServiceConnectionModelTest : ApplicationTest() {

    private lateinit var serviceConnectionModel: ServiceConnectionModel

    @Before
    fun setup() {
        serviceConnectionModel = ServiceConnectionModel()
        serviceConnectionModel.item = ServiceConnection()
    }

    @Test
    fun itShouldBuildApplicationUri() {
        with(serviceConnectionModel.item) {
            protocol = "http"
            host = "foo"
            applicationPort = 1234
        }

        assertEquals(URI("http://foo:1234"), serviceConnectionModel.applicationBaseUrl.value)
    }

    @Test
    fun itShouldBuildAdminUri() {
        with(serviceConnectionModel.item) {
            protocol = "http"
            host = "foo"
            adminPort = 2345
        }

        assertEquals(URI("http://foo:2345"), serviceConnectionModel.adminBaseUrl.value)
    }
}