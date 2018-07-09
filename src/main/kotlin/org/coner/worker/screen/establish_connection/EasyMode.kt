package org.coner.worker.screen.establish_connection

import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.controller.MavenRepo
import org.coner.worker.model.ConerWorkerPropertiesModel
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*

class EasyModeConnectionController : Controller() {
    val model by inject<EasyModeConnectionModel>()
    val coreProcess by di<ConerCoreProcess>()
    val coreServiceConnectionDetailsController by inject<CustomConnectionController>()
    val connectionPreferencesModel by inject<ConnectionPreferencesModel>()

    fun testSettings(spec: AttemptCustomConerCoreConnection) {
        if (coreProcess.started) coreProcess.stop()
        val settings = ConerCoreProcess.Settings(model.pathToJar.value, model.pathToConfig.value)
        coreProcess.configure(settings)
        coreProcess.start()
        coreServiceConnectionDetailsController.connect(spec)
    }

    fun onConnectSuccess(spec: AttemptCustomConerCoreConnection) {
        connectionPreferencesModel.item = ConnectionPreferences().apply {
            mode = ConnectionPreferences.Mode.Easy
            conerCoreServiceUrl = spec.applicationUri.toString()
            conerCoreAdminUrl = spec.adminUri.toString()
            // TODO: paths
        }
    }

    fun onConnectFail(spec: AttemptCustomConerCoreConnection) {
        // TODO
    }
}

class EasyModeConnectionModel : ItemViewModel<ConerCoreProcess.Settings>() {
    val pathToJar = bind(ConerCoreProcess.Settings::pathToJar)
    val pathToConfig = bind(ConerCoreProcess.Settings::pathToConfig)
}

class EasyModeConnectionView : View() {
    val model by inject<EasyModeConnectionModel>()
    val controller by inject<EasyModeConnectionController>()
    val serviceConnectionModel by inject<ServiceConnectionModel>()
    val mavenRepo by inject<MavenRepo>()
    val conerWorkerProperties by inject<ConerWorkerPropertiesModel>()

    override val root = vbox {
        id = "easy_mode"
        button("Resolve Coner Core") {
            action {
                runAsyncWithProgress {
                    mavenRepo.resolve("org.coner:coner-core-service:${conerWorkerProperties.conerCoreVersion}")
                }
            }
        }
    }

    init {
        title = messages["title"]
    }
}