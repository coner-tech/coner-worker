package org.coner.worker.screen.home

import javafx.geometry.Pos
import tornadofx.*

class EventsView : View() {

    override val root = stackpane {
        text(titleProperty) {
            alignment = Pos.TOP_CENTER
        }
    }

    init {
        title = messages["title"]
    }
}