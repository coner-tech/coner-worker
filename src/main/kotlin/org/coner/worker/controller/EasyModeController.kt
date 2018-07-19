package org.coner.worker.controller

import org.coner.worker.ConnectionPreferences
import org.coner.worker.exception.EasyModeException
import org.coner.worker.model.EasyModeModel
import tornadofx.*

class EasyModeController : Controller() {

    val model: EasyModeModel by inject()
    val conerCoreProcessController: ConerCoreProcessController by inject()

    fun start() {
        if (model.started) throw EasyModeException("Easy mode is already started")

        conerCoreProcessController.start()

        model.started = true
    }

    fun stop() {
        if (!model.started) return

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
}

