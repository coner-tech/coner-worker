package org.coner.worker.process

import com.authzee.kotlinguice4.KotlinModule
import com.authzee.kotlinguice4.getInstance
import com.google.inject.Guice
import com.google.inject.Provides
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class ConerCoreProcessTest {

    lateinit var conerCoreProcess: ConerCoreProcess

    lateinit var processBuilder: ProcessBuilder

    @Before
    fun before() {
        processBuilder = ProcessBuilder()
        conerCoreProcess = ConerCoreProcess((processBuilder))
    }

    @Test
    fun itShouldInjectFromGuice() {
        val injector = Guice.createInjector(ConerCoreProcessModule())

        val actual = injector.getInstance<ConerCoreProcess>()

        assertNotNull(actual)
    }

    private class ConerCoreProcessModule : KotlinModule() {
        override fun configure() {
        }

        @Provides
        fun provideProcessBuilder() = ProcessBuilder()
    }
}