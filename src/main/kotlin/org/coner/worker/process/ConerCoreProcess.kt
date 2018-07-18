package org.coner.worker.process

import com.google.common.base.Preconditions
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class ConerCoreProcess @Inject constructor(private val processBuilder: ProcessBuilder) : ManagedProcess() {
    override val started: Boolean
        get() = process?.isAlive == true

    private var process: Process? = null

    init {
        processBuilder.redirectErrorStream(true)
    }

    fun configure(settings: Settings) {
        Preconditions.checkState(!started || process == null, "Can't change settings after starting process")
        processBuilder.command(
                "java", "-jar", settings.pathToJar, "server", settings.pathToConfig
        )
    }

    override fun start() {
        Preconditions.checkState(process == null, "Process already started")
        process = processBuilder.start()
        var verifiedStarted = false
        val buffer = BufferedReader(InputStreamReader(process!!.inputStream))
        var line: String?
        while (!verifiedStarted && process!!.isAlive) {
            line = buffer.readLine()
            verifiedStarted = line?.contains("org.eclipse.jetty.server.Server: Started") == true
        }
        if (!verifiedStarted) throw ManagedProcess.FailedToStartException(this)
    }

    override fun stop() {
        process?.destroy()
        process = null
    }

    data class Settings(val pathToJar: String, val pathToConfig: String)
}
