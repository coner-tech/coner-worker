package org.coner.worker.screen.home

import javafx.geometry.Pos
import tornadofx.*

class SeasonsView : View() {

    override val root = stackpane {
        text(titleProperty) {
            alignment = Pos.TOP_CENTER
        }
    }

    init {
        title = "Seasons"
    }
}