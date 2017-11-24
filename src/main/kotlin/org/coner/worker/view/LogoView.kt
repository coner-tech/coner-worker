package org.coner.worker.view

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.coner.worker.ConerPalette
import tornadofx.*

class LogoView : View() {
    override val root = pane {
        vbox {
            style {
                padding = box(8.px)
            }
            imageview("/coner-logo/coner-logo_96.png")
            text(messages["title_short"]) {
                textAlignment = TextAlignment.CENTER
                font = Font.font("sans-serif", FontWeight.EXTRA_BOLD, 18.0)
                fill = ConerPalette.LOGO_ORANGE
                stroke = Color.BLACK
                strokeWidth = 0.75
                alignment = Pos.CENTER
            }
        }
    }
}
