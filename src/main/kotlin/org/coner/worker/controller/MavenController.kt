package org.coner.worker.controller

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.coner.worker.exception.MavenException
import org.coner.worker.model.MavenModel
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.resolution.ArtifactResult
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferListener
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import tornadofx.*
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicLong

class MavenController : Controller(), TransferListener {
    val model: MavenModel by inject()

    private val repoPath = app.configBasePath.resolve("maven")
    private val repoSystem: RepositorySystem by lazy {
        MavenRepositorySystemUtils.newServiceLocator()
                .addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
                .addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
                .addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
                .getService(RepositorySystem::class.java)
    }

    private val session: RepositorySystemSession by lazy {
        MavenRepositorySystemUtils.newSession().apply {
            isOffline = false
            val localRepo = LocalRepository(repoPath.toFile())
            localRepositoryManager = repoSystem.newLocalRepositoryManager(this, localRepo)
            transferListener = this@MavenController
        }
    }

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
        val collectRequest = CollectRequest()
        val version = model.mavenProperties[artifactKey.versionProperty]
        val artifact = DefaultArtifact("${artifactKey.groupId}:${artifactKey.artifactId}:$version")
        collectRequest.rootArtifact = artifact
        remoteRepos.forEach { collectRequest.addRepository(it) }
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
        AtomicLong(event.resource.contentLength)
        log.info { "transferProgressed: ${event.resource.file.name} (${event.transferredBytes} / ${event.resource?.contentLength})" }
    }

    override fun transferCorrupted(event: TransferEvent?) {
        log.info { "transferCorrupted: $event" }
    }

    override fun transferFailed(event: TransferEvent?) {
        log.info { "transferFailed: $event" }
    }
}
