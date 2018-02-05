package org.coner.worker.process

import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class ConerCoreProcess @Inject constructor(private val processBuilder: ProcessBuilder) : ManagedProcess {

    var process: Process? = null

    init {
        processBuilder.redirectErrorStream(true)
    }

    fun configure(settings: Settings) {
        if (process != null) throw UnsupportedOperationException("Can't change settings after starting process")
        processBuilder.command(
                "java", "-jar", settings.pathToJar, "server", settings.pathToConfig
        )
    }

    override fun start() {
        if (process != null) throw UnsupportedOperationException("Can't start while starting or started")
        process = processBuilder.start()
        var servicesStarted = false
        val buffer = BufferedReader(InputStreamReader(process!!.inputStream))
        var line: String?
        // TODO: timeout, nonblocking, callback for server started?
        while (!servicesStarted && process!!.isAlive) {
            line = buffer.readLine()
            servicesStarted = line.contains("org.eclipse.jetty.server.Server: Started")
        }
        if (!servicesStarted) throw IllegalStateException("Service failed to start")
    }

    override fun stop() {
        process?.destroy()
        process = null
    }

    data class Settings(val pathToJar: String, val pathToConfig: String)
}