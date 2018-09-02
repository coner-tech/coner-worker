package org.coner.worker.widget

import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import org.coner.worker.page.ListMenuNavigationPage
import org.testfx.api.FxRobot
import tornadofx.*

class TestListMenuNavigationApp : App(TestListMenuNavigationMainView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.minWidth = 800.0
        stage.minHeight = 600.0
        stage.height = 600.0
    }
}

class TestListMenuNavigationMainView : View("Test ListMenu Navigation App") {

    private val rootFragmentArgs = mapOf(
            ListMenuNavigationFragment::items to (0..9)
                    .map { ListMenuItem(text = it.toString()) },
            ListMenuNavigationFragment::contentLocator to this::findNumberDisplayFragment
    )

    override val root = find<ListMenuNavigationFragment>(rootFragmentArgs).root.apply {
        id = "list-menu-navigation-main-view"
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

class TestListMenuNavigationMainPage(
        val listMenuNavigation: ListMenuNavigationPage,
        val robot: FxRobot = listMenuNavigation.robot
) {
    val nav = (0..9).map { listMenuNavigation.listItem(it) as ListMenuItem }.toTypedArray()
    fun numberDisplayRoot(): StackPane = robot.from(listMenuNavigation.contentPane).lookup(".number-display-fragment").query()
    fun numberDisplayText(): Text = robot.from(numberDisplayRoot()).lookup("#text").query()
}