package org.coner.worker

import tornadofx.*

class WorkerStylesheet : Stylesheet() {
    init {
        root {
            with (ConerPalette) {
                accentColor = TRAFFIC_CONE_ORANGE
                focusColor = TRAFFIC_CONE_ORANGE
            }
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