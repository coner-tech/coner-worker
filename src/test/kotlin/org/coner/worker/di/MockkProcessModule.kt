package org.coner.worker.di

import com.authzee.kotlinguice4.KotlinModule
import io.mockk.mockk
import org.coner.worker.process.ConerCoreProcess

class MockkProcessModule : KotlinModule() {
    override fun configure() {
        bind<ConerCoreProcess>().toInstance(mockk(relaxed = true))
    }
}