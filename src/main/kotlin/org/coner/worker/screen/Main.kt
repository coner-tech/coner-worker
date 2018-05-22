package org.coner.worker.screen

import javafx.scene.Node
import org.coner.worker.ConerPalette
import org.coner.worker.ConnectionPreferencesController
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
        runLater { controller.afterInit() }
    }
}

class MainCenterView : View() {
    override val root = pane { }
}

class MainController : Controller() {

    val connectionPreferencesController by inject<ConnectionPreferencesController>()

    fun afterInit() {
        if (!connectionPreferencesController.model.item.saved) {
            find(MainCenterView::class).replaceChildren { replaceWith(EstablishConnectionView::class) }
        } else {
            TODO("handle launch with config defined")
        }
    }

}
