package org.coner.worker.process

import org.coner.worker.ConerCoreProcessUtils
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConerCoreProcessIT {

    companion object {
        val settings: ConerCoreProcess.Settings

        init {
            settings = ConerCoreProcess.Settings(
                    pathToJar = ConerCoreProcessUtils.PATH_TO_JAR,
                    pathToConfig = ConerCoreProcessUtils.PATH_TO_CONFIG
            )
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
        assertFalse(conerCoreProcess.started)

        conerCoreProcess.configure(settings)
        conerCoreProcess.start()

        assertTrue(conerCoreProcess.started)

        conerCoreProcess.stop()

        assertFalse(conerCoreProcess.started)
    }
}