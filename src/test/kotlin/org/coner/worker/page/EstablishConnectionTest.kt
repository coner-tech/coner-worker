package org.coner.worker.page

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.TextInputControlMatchers.hasText
import java.net.URI
import kotlin.test.assertEquals

class ConerCoreServiceConnectionDetailsViewTest : ApplicationTest() {

    lateinit var page: ConerCoreServiceConnectionDetailsPage

    override fun start(stage: Stage) {
        super.start(stage)
        val view = ConerCoreServiceConnectionDetailsView()
        stage.scene = Scene(view.root)
        stage.show()
        page = ConerCoreServiceConnectionDetailsPage(this, view)
    }

    override fun stop() {
        super.stop()
        page.view.scope.deregister()
    }

    @Test
    fun itShouldStartWithDefaultValues() {
        // TODO: verify protocol
        verifyThat(page.host, hasText("localhost"))
        verifyThat(page.applicationPort, hasText("8080"))
        verifyThat(page.adminPort, hasText("8081"))
    }

    @Test
    fun itShouldStartWithConnectEnabled() {
        verifyThat(page.connect, NodeMatchers.isEnabled())
    }

    @Test
    fun itShouldDisableConnectWhenHostEmpty() {
        page.clearHost()

        verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldStripWhitespaceFromHost() {
        page.setHost("foo bar")

        verifyThat(page.host, hasText("foobar"))
    }

    @Test
    fun itShouldDisableConnectWhenApplicationPortEmpty() {
        page.clearApplicationPort()

        verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldStripNonIntegerFromApplicationPort() {
        page.setApplicationPort("a.!@1234")

        verifyThat(page.applicationPort, hasText("1234"))
    }

    @Test
    fun itShouldDisableConnectWhenAdminPortEmpty() {
        page.clearAdminPort()

        verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldStripNonIntegerFromAdminPort() {
        page.setAdminPort("a.!@#1234")

        verifyThat(page.adminPort, hasText("1234"))
    }

}

class ConerCoreServiceConnectionDetailsPage(val robot: FxRobot, val view: ConerCoreServiceConnectionDetailsView) {

    val protocol: ChoiceBox<String> = robot.lookup("#protocol").query()
    val applicationPort: TextField = robot.lookup("#application_port").query()
    val adminPort: TextField = robot.lookup("#admin_port").query()
    val host: TextField = robot.lookup("#host").query()
    val connect: Button = robot.lookup("#connect").query()

    fun clearHost() {
        robot.doubleClickOn(host)
        robot.type(KeyCode.BACK_SPACE)
    }

    fun setHost(text: String) {
        clearHost()
        robot.write(text)
    }

    fun clearApplicationPort() {
        robot.doubleClickOn(applicationPort)
        robot.type(KeyCode.BACK_SPACE)
    }

    fun setApplicationPort(text: String) {
        clearApplicationPort()
        robot.write(text)
    }

    fun clearAdminPort() {
        robot.doubleClickOn(adminPort)
        robot.type(KeyCode.BACK_SPACE)
    }

    fun setAdminPort(text: String) {
        clearAdminPort()
        robot.write(text)
    }
}

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