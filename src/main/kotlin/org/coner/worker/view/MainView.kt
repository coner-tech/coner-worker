package org.coner.worker.view

import org.coner.worker.ConerPalette
import tornadofx.*

class MainView : View() {

    override val root = borderpane {
        top {
            hbox {
                style {
                    background = ConerPalette.LOGO_DARK_GRAY.asBackground()
                }
                add(LogoView::class)
            }
        }
        center {

        }
    }
    init {
        title = messages["title"]
    }
}
