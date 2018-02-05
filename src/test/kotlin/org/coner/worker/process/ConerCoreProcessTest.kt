package org.coner.worker.process

import com.authzee.kotlinguice4.KotlinModule
import com.authzee.kotlinguice4.getInstance
import com.google.inject.Guice
import com.google.inject.Provides
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConerCoreProcessTest {

    lateinit var conerCoreProcess: ConerCoreProcess

    lateinit var processBuilder: ProcessBuilder
    val settings = ConerCoreProcess.Settings(
            pathToJar = "/home/cebesius/repos/caeos/coner-core/service/target/coner-core-service-0.1.22-SNAPSHOT.jar",
            pathToConfig = "/home/cebesius/repos/caeos/coner-core/service/src/test/resources/config/test.yml"
    )

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

    @Test
    fun itShouldStartAndStopService() {
        conerCoreProcess.configure(settings)
        conerCoreProcess.start()

        assertTrue(conerCoreProcess.process!!.isAlive)

        conerCoreProcess.stop()

        assertNull(conerCoreProcess.process)
    }

    private class ConerCoreProcessModule : KotlinModule() {
        override fun configure() {
        }

        @Provides
        fun provideProcessBuilder() = ProcessBuilder()
    }
}