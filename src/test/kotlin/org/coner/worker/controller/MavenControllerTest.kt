package org.coner.worker.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.coner.worker.di.GuiceDiContainer
import org.coner.worker.di.MockkProcessModule
import org.coner.worker.model.MavenModel
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.resolution.ArtifactResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.testfx.api.FxToolkit
import tornadofx.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class MavenControllerTest {

    lateinit var controller: MavenController

    lateinit var model: MavenModel
    lateinit var repoSystem: RepositorySystem

    @Rule @JvmField
    val folder = TemporaryFolder()

    @Before
    fun before() {
        val app = object : App() {
            override val configBasePath = folder.root.toPath()
        }
        with(app.scope) {
            set(mockk<MavenModel>(relaxed = true))
        }
        FX.dicontainer = GuiceDiContainer(MockkProcessModule())
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }

        controller = find()

        model = find()
        repoSystem = FX.dicontainer!!.getInstance()
    }

    @After
    fun after() {
        FxToolkit.cleanupApplication(controller.app)
        FxToolkit.cleanupStages()
    }

    @Test
    fun itShouldCreateRepoPathWhenInit() {
        assertThat(controller.repoPath)
                .isNotNull()
                .exists()
                .isDirectory()
                .hasFileName("maven")
    }

    @Test
    fun itShouldHaveRemoteRepos() {
        val field = MavenController::class.declaredMemberProperties.first { it.name == "remoteRepos" }
        field.isAccessible = true

        val remoteRepos: List<RemoteRepository> = field.get(controller) as List<RemoteRepository>

        assertThat(remoteRepos).hasSize(2)
        assertThat(remoteRepos).element(0).extracting("id", "url").containsExactly("jcenter", "https://jcenter.bintray.com/")
        assertThat(remoteRepos).element(1).extracting("id", "url").containsExactly("jfrog-oss-snapshots", "https://oss.jfrog.org/artifactory/oss-snapshot-local")

        field.isAccessible = false
    }

    @Test
    fun itShouldResolve() {
        val artifactKey: MavenModel.ArtifactKey = mockk(relaxed = true)
        val (groupId, artifactId, versionProperty) = arrayOf("mockGroupId", "mockArtifactId", "mockVersionProperty")
        every { artifactKey.groupId }.returns(groupId)
        every { artifactKey.artifactId }.returns(artifactId)
        every { artifactKey.versionProperty }.returns(versionProperty)
        val version = "1.0.0-MOCK"
        every { model.mavenProperties.getString(versionProperty) }.returns(version)
        val result: ArtifactResult = mockk()
        every { repoSystem.resolveArtifact(any(), any()) }.returns(result)

        val actual = controller.resolve(artifactKey)

        assertThat(actual).isSameAs(result)
        val mavenSessionSlot = slot<RepositorySystemSession>()
        val artifactRequestSlot = slot<ArtifactRequest>()
        verify { repoSystem.resolveArtifact(capture(mavenSessionSlot), capture(artifactRequestSlot)) }
    }

}

