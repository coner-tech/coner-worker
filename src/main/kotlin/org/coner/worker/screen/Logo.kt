package org.coner.worker.screen

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.coner.worker.ConerLogoPalette
import tornadofx.*

class LogoView : View() {

    override val root = pane {
        vbox {
            style {
                padding = box(8.px)
            }
            imageview("/coner-logo/coner-logo_96.png") {
                id = "logo"
            }
            text(messages["title_short"]) {
                id = "title_short"
                textAlignment = TextAlignment.CENTER
                font = Font.font("sans-serif", FontWeight.EXTRA_BOLD, 18.0)
                fill = ConerLogoPalette.ORANGE
                stroke = Color.BLACK
                strokeWidth = 0.75
                alignment = Pos.CENTER
            }
        }
    }
}
