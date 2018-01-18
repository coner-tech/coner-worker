package org.coner.worker

import javafx.scene.text.FontWeight
import tornadofx.*

class WorkerStylesheet : Stylesheet() {

    companion object {
        val h1 by cssclass("h1")
    }

    init {
        root {
            with(ConerPalette) {
                accentColor = LOGO_ORANGE
                focusColor = LOGO_ORANGE
            }
        }
        h1 {
            fontSize = 18.pt
            fontWeight = FontWeight.BOLD
            padding = box(0.px, 8.px)
        }
    }
}

class ConerPalette {
    companion object {
        val TRAFFIC_CONE_ORANGE = c("#FF7900")
        val LOGO_ORANGE = c("#F15A24")
        val LOGO_ORANGE_SHADOW = c("#D33C0D")
        val LOGO_DARK_GRAY = c("#808080")
    }
}