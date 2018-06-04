package org.coner.worker.di

import com.google.inject.Guice
import com.google.inject.Module
import tornadofx.*
import kotlin.reflect.KClass

class GuiceDiContainer(vararg modules: Module) : DIContainer {
    private val guice = Guice.createInjector(*modules)

    override fun <T : Any> getInstance(type: KClass<T>): T {
        return guice.getInstance(type.java)
    }
}