package org.coner.worker.controller

import javafx.beans.property.Property
import org.coner.worker.ConnectionPreferences
import org.coner.worker.exception.EasyModeException
import org.coner.worker.model.EasyModeModel
import tornadofx.*

class EasyModeController : Controller() {

    val model: EasyModeModel by inject()
    val conerCoreProcessController: ConerCoreProcessController by inject()

    fun start(stepProperty: Property<StartStep?>? = null) {
        if (model.started) throw EasyModeException("Easy mode is already started")

        runLater { stepProperty?.value = StartStep.RESOLVE }
        conerCoreProcessController.resolve()

        runLater { stepProperty?.value = StartStep.START }
        conerCoreProcessController.start()

        runLater { stepProperty?.value = StartStep.HEALTH_CHECK }
        conerCoreProcessController.checkHealth()

        runLater { stepProperty?.value = null }
        model.started = true
    }

    fun stop() {
        conerCoreProcessController.stop()

        model.started = false
    }

    fun checkHealth() {
        conerCoreProcessController.checkHealth()
    }

    fun buildConnectionPreferences(): ConnectionPreferences {
        return ConnectionPreferences(
                saved = false,
                mode = ConnectionPreferences.Mode.EASY,
                conerCoreServiceUrl = conerCoreProcessController.model.serviceUrl,
                conerCoreAdminUrl = conerCoreProcessController.model.adminUrl
        )
    }

    enum class StartStep {
        RESOLVE,
        START,
        HEALTH_CHECK
    }
}

