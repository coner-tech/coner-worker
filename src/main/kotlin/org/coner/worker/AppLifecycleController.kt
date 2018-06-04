package org.coner.worker

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.WindowEvent
import org.coner.worker.process.ConerCoreProcess
import tornadofx.*

class AppLifecycleController : Controller() {

    val conerCoreProcess: ConerCoreProcess by di()

    fun onCloseRequest(windowEvent: WindowEvent) {
        if (!conerCoreProcess.started) return
        alert(
                type = Alert.AlertType.CONFIRMATION,
                title = messages["close_alert_title"],
                header = messages["close_alert_header"],
                content = messages["close_alert_content"],
                actionFn = {
                    if (it != ButtonType.OK) {
                        windowEvent.consume()
                        return
                    }
                    conerCoreProcess.stop()
                }
        ).show()
    }
}