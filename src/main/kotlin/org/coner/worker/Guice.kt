package org.coner.worker

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides

class AppModule : Module {
    override fun configure(binder: Binder) {

    }

    @Provides
    fun processBuilder() = ProcessBuilder()

}