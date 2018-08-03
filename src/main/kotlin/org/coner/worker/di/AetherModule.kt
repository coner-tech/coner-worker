package org.coner.worker.di

import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory

class AetherModule : AbstractModule() {
    @Provides
    fun repositorySystem() = MavenRepositorySystemUtils.newServiceLocator()
            .addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
            .addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
            .addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
            .getService(RepositorySystem::class.java)

}