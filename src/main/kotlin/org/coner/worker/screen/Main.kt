package org.coner.worker.screen

import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeType
import javafx.stage.WindowEvent
import org.coner.worker.ConerLogoPalette
import org.coner.worker.ConnectionPreferencesController
import org.coner.worker.WorkerStylesheet
import org.coner.worker.controller.EasyModeController
import org.coner.worker.screen.establish_connection.EstablishConnectionView
import org.coner.worker.screen.home.HomeView
import tornadofx.*

class MainView : View() {

    val controller: MainController by inject()

    override val root = borderpane {
        top {
            hbox {
                background = ConerLogoPalette.DARK_GRAY.asBackground()
                padding = insets(8.0)
                stackpane {
                    text(find<MainCenterView>().titleProperty) {
                        addClass(WorkerStylesheet.h1)
                        fill = ConerLogoPalette.ORANGE
                        stroke = Color.BLACK
                        strokeWidth = 0.75
                        strokeType = StrokeType.OUTSIDE
                        alignment = Pos.CENTER_RIGHT
                        effect = DropShadow().apply {
                            color = Color.BLACK
                            offsetX = 2.0
                            offsetY = -2.0
                            radius = 4.0
                        }
                    }
                }
                pane {
                    hgrow = Priority.ALWAYS
                }
                add<LogoView>()
            }
        }
        center<MainCenterView>()
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
                actionFn = { if (it == ButtonType.OK) onConfirmed() else onCancelled() },
                owner = currentWindow
        )
    }
}

class MainCenterView : View() {

    override val root = stackpane { }

    init {
        root.children.onChange { change ->
            while (change.next()) {
                if (change.wasAdded()) {
                    val uiComponent = root.children.first().uiComponent<UIComponent>()!!
                    titleProperty.unbind()
                    titleProperty.bind(uiComponent.titleProperty)
                }
            }
        }
    }

}

class MainController : Controller() {

    val connectionPreferencesController by inject<ConnectionPreferencesController>()
    val easyMode: EasyModeController by inject()

    fun onViewInit() {
        if (!connectionPreferencesController.model.item.saved) {
            find<MainCenterView>().root.replaceChildren(find<EstablishConnectionView>())
            connectionPreferencesController.model.itemProperty.onChangeOnce {
                navigateToHome()
            }
        } else {
            TODO("handle launch with config defined")
        }
    }

    fun onCloseRequest(windowEvent: WindowEvent) {
        if (!easyMode.model.started) return
        find<MainView>().showCloseRequestConfirmation(
                onConfirmed = { easyMode.stop() },
                onCancelled = { windowEvent.consume() }
        )
    }

    fun navigateToHome() {
        val home = find<HomeView>()
        find<MainCenterView>().root.children.first().replaceWith(home.root)
    }

}
