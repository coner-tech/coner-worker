package org.coner.worker.process

import org.apache.commons.io.IOUtils
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertTrue

class ExploratoryProcessTest {

    @Rule
    @JvmField
    val folder = TemporaryFolder()

    @Test
    fun itShouldReadFromRedirectedOutputFile() {
        val outputFile = folder.newFile()
        val process = ProcessBuilder("java", "-version")
                .redirectErrorStream(true)
                .redirectOutput(outputFile)
                .start()
        process.waitFor()
        val output = outputFile.readText()
        assertTrue(output.contains("java", ignoreCase = true))
    }

    @Test
    fun itShouldReadFromOutputStream() {
        val process = ProcessBuilder("java", "-version")
                .redirectErrorStream(true)
                .start()
        process.waitFor()
        val output = IOUtils.toString(process.inputStream, Charsets.UTF_8)
        assertTrue(output.contains("java", ignoreCase = true))
    }
}