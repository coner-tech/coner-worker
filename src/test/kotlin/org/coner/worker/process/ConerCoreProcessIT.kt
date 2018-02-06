package org.coner.worker.process

import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConerCoreProcessIT {

    lateinit var conerCoreProcess: ConerCoreProcess

    lateinit var processBuilder: ProcessBuilder
    val settings = ConerCoreProcess.Settings(
            pathToJar = "it/environment/coner-core-service-${System.getProperty("coner-core.version")}.jar",
            pathToConfig = "it/environment/test.yml"
    )

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