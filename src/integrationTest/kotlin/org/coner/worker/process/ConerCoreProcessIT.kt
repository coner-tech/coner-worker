package org.coner.worker.process

import com.google.common.base.Preconditions
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Test for ConerCoreProcess
 *
 * WARNING: this test depends on the core service jar existing at `pathToJar` relative to the project root, and the
 * version passed in either as an environment variable or system property.
 *
 * Maven will set up the dependencies during `pre-integration-test`, and the POM configures the Failsafe plugin to
 * pass the needed path property during `integration-test`.
 *
 * IntelliJ users: run `./mvnw pre-integration-test` before attempting to run this test in IntelliJ. Also, configure
 * your Run Configuration for this test to pass the needed version in as an environment variable.
**/
class ConerCoreProcessIT {

    companion object {
        private val VERSION_PROPERTY = "coner-core.version"

        val settings: ConerCoreProcess.Settings

        init {
            val version = System.getenv(VERSION_PROPERTY) ?: System.getProperty(VERSION_PROPERTY)
            settings = ConerCoreProcess.Settings(
                    pathToJar = "it/environment/coner-core-service-$version.jar",
                    pathToConfig = "it/environment/test.yml"
            )
        }

        @BeforeClass @JvmStatic
        fun beforeClass() {
            Preconditions.checkState(Files.exists(Paths.get(settings.pathToJar)), "jar missing: %s", settings.pathToJar)
            Preconditions.checkState(Files.exists(Paths.get(settings.pathToConfig)), "config missing: %s", settings.pathToConfig)
        }
    }

    lateinit var conerCoreProcess: ConerCoreProcess
    lateinit var processBuilder: ProcessBuilder

    @Before
    fun before() {
        processBuilder = ProcessBuilder()
        conerCoreProcess = ConerCoreProcess((processBuilder))
    }

    @Test
    fun itShouldStartAndStopService() {
        conerCoreProcess.configure(settings)
        conerCoreProcess.start()

        assertTrue(conerCoreProcess.process!!.isAlive)

        conerCoreProcess.stop()

        assertNull(conerCoreProcess.process)
    }
}