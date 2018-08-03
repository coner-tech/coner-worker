package org.coner.worker.controller

import org.coner.worker.di.wrapper.MavenRepositorySystemUtilsWrapper
import org.coner.worker.exception.MavenException
import org.coner.worker.model.MavenModel
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.resolution.ArtifactResult
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferListener
import tornadofx.*
import java.nio.file.Files

class MavenController : Controller(), TransferListener {
    val model: MavenModel by inject()

    val repoPath = app.configBasePath.resolve("maven")
    private val repoSystem: RepositorySystem by di()
    private val mavenRepositorySystemUtilsWrapper: MavenRepositorySystemUtilsWrapper by di()

    private val remoteRepos: List<RemoteRepository> by lazy {
        fun remoteRepo(id: String, url: String): RemoteRepository {
            return RemoteRepository.Builder(id, "default", url).build()
        }
        listOf(
                remoteRepo("jcenter", "https://jcenter.bintray.com/"),
                remoteRepo("jfrog-oss-snapshots", "https://oss.jfrog.org/artifactory/oss-snapshot-local")
        )
    }

    init {
        Files.createDirectories(repoPath)
    }

    fun resolve(artifactKey: MavenModel.ArtifactKey): ArtifactResult {
        val session = mavenRepositorySystemUtilsWrapper.newSession().apply {
            isOffline = false
            val localRepo = LocalRepository(repoPath.toFile())
            localRepositoryManager = repoSystem.newLocalRepositoryManager(this, localRepo)
            transferListener = this@MavenController
        }
        val version = model.mavenProperties.getString(artifactKey.versionProperty)
        val artifact = DefaultArtifact("${artifactKey.groupId}:${artifactKey.artifactId}:$version")
        try {
            return repoSystem.resolveArtifact(session, ArtifactRequest(artifact, remoteRepos, null))
        } catch (t: Exception) {
            throw MavenException("Failed to resolve artifact: $artifact", t)
        }
    }

    override fun transferStarted(event: TransferEvent?) {
        log.info { "transferStarted: $event" }
    }

    override fun transferInitiated(event: TransferEvent?) {
        log.info { "transferInitiated: $event" }
    }

    override fun transferSucceeded(event: TransferEvent?) {
        log.info { "transferSucceeded: $event" }
    }

    override fun transferProgressed(event: TransferEvent) {
        log.info { "transferProgressed: ${event.resource.file.name} (${event.transferredBytes} / ${event.resource?.contentLength})" }
    }

    override fun transferCorrupted(event: TransferEvent?) {
        log.info { "transferCorrupted: $event" }
    }

    override fun transferFailed(event: TransferEvent?) {
        log.info { "transferFailed: $event" }
    }
}
