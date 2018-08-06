package org.coner.worker.controller

import com.google.common.base.Preconditions
import org.coner.worker.exception.EasyModeException
import org.coner.worker.model.ConerCoreProcessModel
import org.coner.worker.model.MavenModel
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*
import java.io.File

class ConerCoreProcessController : Controller() {

    val model: ConerCoreProcessModel by inject()

    private val process: ConerCoreProcess by di()
    private val maven: MavenController by inject()
    private val adminApi: ConerCoreAdminApi by inject()


    init {
        adminApi.baseURI = model.adminUrl.toString()
    }

    fun resolve() {
        val result = maven.resolve(MavenModel.ArtifactKey.ConerCoreService)
        model.jarFile = result.artifact.file.absolutePath
        model.configFile = buildConfigFile().absolutePath
    }

    fun start() {
        Preconditions.checkNotNull(model.jarFile, "jarFile not resolved")
        Preconditions.checkNotNull(model.configFile, "configFile not resovled")

        process.configure(ConerCoreProcess.Settings(model.jarFile, model.configFile))
        process.start()
    }

    fun stop() {
        process.stop()
    }

    fun checkHealth() {
        val response = adminApi.get("/healthcheck")
        try {
            if (!response.ok()) {
                throw EasyModeException("Failed to check health. ${response.status} ${response.reason}")
            }
        } finally {
            response.consume()
        }
    }

    fun buildConfigFile(): File {
        val configFileFolder = app.configBasePath.resolve("easy-mode").toFile()
        if (!configFileFolder.exists()) configFileFolder.mkdirs()
        val configFileName = "coner-core-service.yml"
        val configFile = File(configFileFolder, configFileName)

        val databaseUrlFileName = "coner-core-service.db"
        val databaseUrlFile = File(configFileFolder, databaseUrlFileName)

        var out = resources.text("/easy-mode/$configFileName.template")
        out = out.replace("{{server.applicationConnectors[0].port}}", "${model.serviceUrl.port}")
        out = out.replace("{{server.adminConnectors[0].port}}", "${model.adminUrl.port}")
        out = out.replace("{{database.url.file}}", databaseUrlFile.absolutePath)
        configFile.writeText(out)

        return configFile
    }
}

class ConerCoreAdminApi : Rest()