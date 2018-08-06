package org.coner.worker.di

import com.authzee.kotlinguice4.KotlinModule
import io.mockk.mockk
import org.coner.worker.process.ConerCoreProcess
import org.eclipse.aether.RepositorySystem

class MockkProcessModule : KotlinModule() {
    override fun configure() {
        bind<ConerCoreProcess>().toInstance(mockk(relaxed = true))
        bind<RepositorySystem>().toInstance(mockk(relaxed = true))
    }
}