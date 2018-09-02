package org.coner.worker.widget

import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.Duration
import tornadofx.*

class ListMenuNavigationFragment : Fragment() {

    private lateinit var contentPane: Pane
    private lateinit var selected: Parent
    private lateinit var listMenu: ListMenu
    val adapter: Adapter by param()

    override val root = hbox {
        scrollpane(fitToWidth = true, fitToHeight = true) {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            listmenu(orientation = Orientation.VERTICAL) {
                listMenu = this
                this@scrollpane.minWidthProperty().bind(
                        this.widthProperty()
                                .plus(this@scrollpane.paddingLeftProperty)
                                .plus(this@scrollpane.paddingRightProperty)
                )
                for (i in adapter.range) {
                    val uiComponent = adapter.locate(i)
                    item {
                        textProperty.bind(uiComponent.titleProperty)
                    }
                }
            }
        }
        stackpane {
            contentPane = this
            hgrow = Priority.ALWAYS
            addClass("content-pane")
        }
        addClass("list-menu-navigation-fragment")
    }

    override fun onDock() {
        super.onDock()
        listMenu.activeItemProperty.addListener(activeItemChangedListener)
        listMenu.activeItem = listMenu.items[0]
    }

    override fun onUndock() {
        super.onUndock()
        listMenu.activeItemProperty.removeListener(activeItemChangedListener)
    }

    private val activeItemChangedListener = ChangeListener<ListMenuItem?> { observable, oldValue, newValue ->
        if (oldValue == newValue) return@ChangeListener
        val oldIndex = listMenu.items.indexOf(oldValue)
        val newIndex = listMenu.items.indexOf(newValue)
        val replacement = adapter.locate(newIndex)
        if (oldIndex >= 0) {
            val direction = if (newIndex > oldIndex) {
                ViewTransition.Direction.UP
            } else {
                ViewTransition.Direction.DOWN
            }
            val transition = ViewTransition.Metro(
                    duration = Duration.millis(300.0),
                    distancePercentage = 0.33,
                    direction = direction
            )
            selected.replaceWith(
                    replacement = replacement.root,
                    transition = transition
            ) {
                selected = replacement.root
            }
        } else {
            runLater {
                contentPane.add(replacement)
                selected = replacement.root
            }
        }
    }

    data class Adapter(val count: Int, private val locator: (index: Int) -> UIComponent) {
        val range = 0..(count - 1)

        fun locate(index: Int): UIComponent {
            check(range.contains(index)) { "$index out of range: $range" }
            return locator(index)
        }
    }
}