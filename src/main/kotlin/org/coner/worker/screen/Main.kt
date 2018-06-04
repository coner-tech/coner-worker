package org.coner.worker.screen

import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.WindowEvent
import org.coner.worker.ConerPalette
import org.coner.worker.ConnectionPreferencesController
import org.coner.worker.process.ConerCoreProcess
import org.coner.worker.screen.establish_connection.EstablishConnectionView
import tornadofx.*

class MainView : View() {

    val controller: MainController by inject()
    lateinit var center: Node

    override val root = borderpane {
        top {
            hbox {
                style {
                    background = ConerPalette.LOGO_DARK_GRAY.asBackground()
                }
                add(LogoView::class)
            }
        }
        center(MainCenterView::class)
    }

    init {
        title = messages["title"]
        controller.onViewInit()
    }

    fun showCloseRequestConfirmation(onConfirmed: () -> Unit, onCancelled: () -> Unit) {
        alert(
                type = Alert.AlertType.CONFIRMATION,
                title = messages["close_alert_title"],
                header = messages["close_alert_header"],
                content = messages["close_alert_content"],
                actionFn = { if (it == ButtonType.OK) onConfirmed() else onCancelled() }
        )
    }
}

class MainCenterView : View() {
    override val root = pane { }
}

class MainController : Controller() {

    val connectionPreferencesController by inject<ConnectionPreferencesController>()
    val conerCoreProcess: ConerCoreProcess by di()

    fun onViewInit() {
        if (!connectionPreferencesController.model.item.saved) {
            find(MainCenterView::class).replaceChildren { replaceWith(EstablishConnectionView::class) }
        } else {
            TODO("handle launch with config defined")
        }
    }

    fun onCloseRequest(windowEvent: WindowEvent) {
        if (!conerCoreProcess.started) return
        find<MainView>().showCloseRequestConfirmation(
                onConfirmed = { conerCoreProcess.stop() },
                onCancelled = { windowEvent.consume() }
        )
    }

}
