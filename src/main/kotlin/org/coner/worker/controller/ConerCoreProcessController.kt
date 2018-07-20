package org.coner.worker.controller

import com.google.common.base.Preconditions
import org.coner.worker.exception.EasyModeException
import org.coner.worker.model.ConerCoreProcessModel
import org.coner.worker.model.MavenModel
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*

class ConerCoreProcessController : Controller() {

    val model: ConerCoreProcessModel by inject()

    private val process: ConerCoreProcess by di()
    private val maven: MavenController by inject()
    private val adminApi: ConerCoreAdminApi by inject()


    init {
        adminApi.baseURI = "http://localhost:8081"
    }

    fun resolve() {
        val result = maven.resolve(MavenModel.ArtifactKey.ConerCoreService)
        model.jarFile = result.artifact.file.absolutePath
        model.configFile = "it/config/coner-core-service.yml" // TODO: unpack from compiled resource
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
}

class ConerCoreAdminApi : Rest()