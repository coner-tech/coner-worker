package org.coner.worker.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.coner.worker.di.GuiceDiContainer
import org.coner.worker.di.MockkProcessModule
import org.coner.worker.exception.EasyModeException
import org.coner.worker.model.ConerCoreProcessModel
import org.coner.worker.model.MavenModel
import org.coner.worker.process.ConerCoreProcess
import org.eclipse.aether.resolution.ArtifactResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.testfx.api.FxToolkit
import tornadofx.*

class ConerCoreProcessControllerTest {

    lateinit var controller: ConerCoreProcessController

    lateinit var model: ConerCoreProcessModel
    lateinit var process: ConerCoreProcess
    lateinit var maven: MavenController
    lateinit var adminApi: ConerCoreAdminApi

    @Rule @JvmField
    val folder = TemporaryFolder()

    @Before
    fun before() {
        val app = object : App() {
            override val configBasePath = folder.root.toPath()
        }
        with(app.scope) {
            set(mockk<MavenController>(relaxed = true, name = "maven"))
            set(mockk<ConerCoreAdminApi>(relaxed = true, name = "adminApi"))
        }
        val di = GuiceDiContainer(MockkProcessModule())
        FX.dicontainer = di
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }

        controller = find()

        model = find()
        process = di.getInstance()
        maven = find()
        adminApi = find()
    }

    @After
    fun after() {
        FX.dicontainer = null
        FxToolkit.cleanupApplication(controller.app)
        FxToolkit.cleanupStages()
    }

    @Test
    fun whenInitItShouldAssignDefaultAdminApiBaseUri() {
        verify { adminApi.baseURI = "http://localhost:8081" }
    }

    @Test
    fun itShouldResolve() {
        val result: ArtifactResult = mockk()
        val conerCoreArtifactFile = folder.newFile("coner-core-service-someversion.jar")
        every { maven.resolve(MavenModel.ArtifactKey.ConerCoreService) }.returns(result)
        every { result.artifact.file }.returns(conerCoreArtifactFile)

        controller.resolve()

        verify { maven.resolve(MavenModel.ArtifactKey.ConerCoreService) }
        assertThat(model.jarFile)
                .isNotNull()
                .isSameAs(conerCoreArtifactFile.absolutePath)
        assertThat(model.configFile)
                .isNotNull()
                .startsWith(folder.root.absolutePath)
                .endsWith("coner-core-service.yml")
    }

    @Test
    fun itShouldCheckHealthWhenHealthy() {
        val response: Rest.Response = mockk(relaxed = true)
        every { adminApi.get("/healthcheck" )}.returns(response)
        every { response.ok() }.returns(true)

        controller.checkHealth()

        verify { response.ok() }
        verify { response.consume() }
    }

    @Test
    fun itShouldCheckHealthWhenUnhealthy() {
        val response: Rest.Response = mockk(relaxed = true)
        every { adminApi.get("/healthcheck" )}.returns(response)
        every { response.ok() }.returns(false)
        val responseStatus = Rest.Response.Status.InternalServerError
        every { response.status }.returns(responseStatus)
        val responseReason = "Oops something went wrong"
        every { response.reason }.returns(responseReason)

        try {
            controller.checkHealth()
            failBecauseExceptionWasNotThrown(EasyModeException::class.java)
        } catch (t: Throwable) {
            assertThat(t)
                    .isInstanceOf(EasyModeException::class.java)
                    .hasMessageContaining(responseStatus.toString())
                    .hasMessageContaining(responseReason)
        } finally {
            verify { response.ok() }
        }

    }

    @Test
    fun whenBuildConfigFileItShouldCreateEasyModeFolderWithConfigs() {
        val actual = controller.buildConfigFile()

        val easyModeFolder = folder.root.resolve("easy-mode")
        assertThat(easyModeFolder)
                .exists()
                .isDirectory()
        val easyModeDb = easyModeFolder.resolve("coner-core-service.db")
        val easyModeConfig = easyModeFolder.resolve("coner-core-service.yml")
        assertThat(easyModeConfig)
                .exists()
                .isFile()
        assertThat(actual).hasSameContentAs(easyModeConfig)
        val easyModeConfigActual = easyModeConfig.readText()
        assertThat(easyModeConfigActual)
                .contains(
                        "port: 8080",
                        "port: 8081",
                        "url: jdbc:hsqldb:file:${easyModeDb.absolutePath}"
                )
    }

    @Test
    fun itShouldStartWhenResolved() {
        // mock state after resolve()
        val pathToJar = folder.newFile("coner-core-service.mock.jar").absolutePath
        model.jarFile = pathToJar
        val pathToConfig = folder.newFile("coner-core-service.mock.config").absolutePath
        model.configFile = pathToConfig

        controller.start()

        verify { process.configure(eq(ConerCoreProcess.Settings(pathToJar, pathToConfig))) }
        verify { process.start() }
    }

    @Test
    fun itShouldStop() {
        controller.stop()

        verify { process.stop() }
    }

}