package org.coner.worker.controller

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import tornadofx.*

class MavenRepo : Controller() {

    private val repoSystem: RepositorySystem by lazy {
        MavenRepositorySystemUtils.newServiceLocator().apply {
            addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
            addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
            addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
        }.getService(RepositorySystem::class.java)
    }

    private val session: RepositorySystemSession by lazy {
        MavenRepositorySystemUtils.newSession().apply {
            isOffline = false
            val localRepo = LocalRepository(app.configBasePath.resolve("maven").toFile())
            localRepositoryManager = repoSystem.newLocalRepositoryManager(this, localRepo)
        }
    }

    fun resolve(artifact: String) {
        val dependency = Dependency(DefaultArtifact(artifact), "runtime")
        val jcenter = RemoteRepository.Builder("jcenter", "default", "http://jcenter.bintray.com")
                .build()
        val collectRequest = CollectRequest().apply {
            root = dependency
            addRepository(jcenter)
        }
        val node = repoSystem.collectDependencies(session, collectRequest).root
        val dependencyRequest = DependencyRequest().apply {
            root = node
        }
        val dependencyResult = repoSystem.resolveDependencies(session, dependencyRequest)
        dependencyResult.artifactResults.forEach {
            println(">>> ArtifactResult")
            println("GroupId: ${it.artifact}")
            println("<<< ArtifactResult")
        }

    }
}