package org.coner.worker.screen.establish_connection

import org.coner.worker.page.EasyModeConnectionPage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.TextInputControlMatchers
import tornadofx.*
import java.io.File

class EasyModeConnectionViewTest {

    lateinit var view: EasyModeConnectionView
    lateinit var page: EasyModeConnectionPage
    lateinit var validPathToJar: File
    lateinit var validPathToConfig: File

    @Rule
    @JvmField
    val folder = TemporaryFolder()

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 600.0
        val app = App(EasyModeConnectionView::class)
        with(app.scope) {
            // TODO: injections?
        }
        FxToolkit.setupApplication { app }
        view = stage.uiComponent()!!
        page = EasyModeConnectionPage(FxRobot())
        validPathToJar = folder.newFile(page.realisticValues.jarName)
        validPathToConfig = folder.newFile(page.realisticValues.configName)
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }

    @Test
    fun itShouldStartWithDefaultValues() {
        FxAssert.verifyThat(page.pathToJar, TextInputControlMatchers.hasText(null as String?))
        FxAssert.verifyThat(page.pathToConfig, TextInputControlMatchers.hasText(null as String?))
        FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled())
    }

    @Test
    fun itShouldEnableConnectWhenPageFilledRealisticValues() {
        page.fillValues(validPathToJar.absolutePath, validPathToConfig.absolutePath)

        FX.runAndWait { FxAssert.verifyThat(page.connect, NodeMatchers.isEnabled()) }
    }

    @Test
    fun itShouldEnableConnectWhenModelFilledRealisticValues() {
        view.model.fillValidValues()

        FX.runAndWait { FxAssert.verifyThat(page.connect, NodeMatchers.isEnabled()) }
    }

    @Test
    fun itShouldDisableConnectWhenPathToJarEmpty() {
        view.model.fillValidValues()

        page.clearJar()

        FX.runAndWait { FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled()) }
    }

    @Test
    fun itShouldDisableConnectWhenPathToJarFileNotExist() {
        view.model.fillValidValues()

        FX.runAndWait { view.model.pathToJar.value = "${view.model.pathToJar.value}.nope" }

        FX.runAndWait { FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled()) }
    }

    @Test
    fun itShouldDisableConnectWhenPathToConfigEmpty() {
        view.model.fillValidValues()

        page.clearConfig()

        FX.runAndWait { FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled()) }
    }

    @Test
    fun itShouldDisableConnectWhenPathToConfigFileNotExist() {
        view.model.fillValidValues()

        FX.runAndWait { view.model.pathToConfig.value = "${view.model.pathToConfig.value}.nope" }

        FX.runAndWait { FxAssert.verifyThat(page.connect, NodeMatchers.isDisabled()) }
    }

    private fun EasyModeConnectionModel.fillValidValues() {
        FX.runAndWait {
            pathToJar.value = validPathToJar.absolutePath
            pathToConfig.value = validPathToConfig.absolutePath
        }
    }
}
