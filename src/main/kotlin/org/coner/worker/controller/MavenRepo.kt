package org.coner.worker.controller

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import tornadofx.*
import java.nio.file.Files

class MavenRepo : Controller() {

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
        }
    }

    private val remoteRepos: List<RemoteRepository> by lazy {
        fun remoteRepo(id: String, url: String): RemoteRepository {
            return RemoteRepository.Builder(id, "default", url).build()
        }
        listOf(
                remoteRepo("jcenter", "http://jcenter.bintray.com/"),
                remoteRepo("jfrog-oss-snapshots", "https://oss.jfrog.org/artifactory/oss-snapshot-local")
        )
    }

    init {
        Files.createDirectories(repoPath)
    }

    fun resolve(artifactAsString: String) {
        val collectRequest = CollectRequest()
        val artifact = DefaultArtifact(artifactAsString)
        collectRequest.rootArtifact = artifact
        remoteRepos.forEach { collectRequest.addRepository(it) }
        val result = repoSystem.collectDependencies(session, collectRequest)
        println("result: $result")
        val resolveDependenciesResult = repoSystem.resolveArtifact(session, ArtifactRequest(artifact, remoteRepos, null))
        println("resolveDependenciesResult: $resolveDependenciesResult")
    }
}