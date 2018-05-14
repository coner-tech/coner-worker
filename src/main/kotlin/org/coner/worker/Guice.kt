package org.coner.worker

import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.coner.worker.process.ConerCoreProcess

class AppModule : AbstractModule() {
    override fun configure() {
        bind(ConerCoreProcess::class.java).asEagerSingleton()
    }

    @Provides
    fun processBuilder() = ProcessBuilder()

}