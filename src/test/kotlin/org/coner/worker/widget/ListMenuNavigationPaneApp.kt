package org.coner.worker.widget

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import tornadofx.*

class ListMenuNavigationPaneApp : App(ListMenuNavigationPaneAppMainView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.minWidth = 320.0
        stage.minHeight = 240.0
        stage.height = 240.0
    }
}

class ListMenuNavigationPaneAppMainView : View("Navigation Pane App") {

    override val root = find<ListMenuNavigationPaneFragment>(
            ListMenuNavigationPaneFragment::items to (0..9)
                    .map { ListMenuItem(text = it.toString()) }.toTypedArray(),
            ListMenuNavigationPaneFragment::contentLocator to this::findNumberDisplayFragment
    ).root

    private fun findNumberDisplayFragment(item: ListMenuItem): UIComponent {
        return find<NumberDisplayFragment>(
                NumberDisplayFragment::digit to item.text!!.toInt()
        )
    }
}

class NumberDisplayFragment : Fragment() {
    val digit: Int by param()
    val converter: NumberDisplayConverter by inject()
    val text = converter.convert(digit)

    override val root = stackpane {
        alignment = Pos.CENTER
        text(text) {
            style {
                fontSize = 48.0.pt
                fontWeight = FontWeight.BOLD
            }
        }
    }

    override fun onDock() {
        super.onDock()
        root.prefWidthProperty().bind(root.widthProperty())
        root.prefHeightProperty().bind(root.heightProperty())
    }

    override fun onUndock() {
        super.onUndock()
        root.prefWidthProperty().unbind()
        root.prefHeightProperty().unbind()
    }
}

class NumberDisplayConverter : Controller() {
    fun convert(integer: Int) = when (integer) {
        0 -> "Zero"
        1 -> "One"
        2 -> "Two"
        3 -> "Three"
        4 -> "Four"
        5 -> "Five"
        6 -> "Six"
        7 -> "Seven"
        8 -> "Eight"
        9 -> "Nine"
        else -> throw IllegalStateException("Only support range 0 .. 9")
    }

}