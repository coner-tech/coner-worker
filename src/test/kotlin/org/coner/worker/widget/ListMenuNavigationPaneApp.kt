package org.coner.worker.widget

import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import org.coner.worker.page.ListMenuNavigationPanePage
import org.testfx.api.FxRobot
import tornadofx.*

class ListMenuNavigationPaneApp : App(ListMenuNavigationPaneAppMainView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.minWidth = 800.0
        stage.minHeight = 600.0
        stage.height = 600.0
    }
}

class ListMenuNavigationPaneAppMainView : View("Navigation Pane App") {

    override val root = find<ListMenuNavigationPaneFragment>(
            ListMenuNavigationPaneFragment::items to (0..9)
                    .map { ListMenuItem(text = it.toString()) },
            ListMenuNavigationPaneFragment::contentLocator to this::findNumberDisplayFragment
    ).root.apply {
        id = "list-menu-navigation-pane-app-main-view"
    }

    fun findNumberDisplayFragment(item: ListMenuItem): UIComponent {
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
        addClass("number-display-fragment")
        alignment = Pos.CENTER
        text(text) {
            id = "text"
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

class ListMenuNavigationPaneAppMainPage(
        val listMenuNavigationPane: ListMenuNavigationPanePage,
        val robot: FxRobot = listMenuNavigationPane.robot
) {
    val nav = (0 .. 9).map { listMenuNavigationPane.listItem(it) as ListMenuItem }.toTypedArray()
    fun numberDisplayRoot(): StackPane = robot.from(listMenuNavigationPane.contentPane).lookup(".number-display-fragment").query()
    fun numberDisplayText(): Text = robot.from(numberDisplayRoot()).lookup("#text").query()
}