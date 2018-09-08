package org.coner.worker.screen.home

import tornadofx.*

class HomeView : View() {
    override val root = stackpane {
        text("Home")
    }

    init {
        title = "Home"
    }
}