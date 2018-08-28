package org.coner.worker.widget

import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.Duration
import tornadofx.*

class ListMenuNavigationPaneFragment : Fragment() {

    private lateinit var contentPane: Pane
    private lateinit var selected: Parent
    private lateinit var listMenu: ListMenu
    val items: Array<ListMenuItem> by param()
    val contentLocator: (item: ListMenuItem) -> UIComponent by param()

    override val root = hbox {
        scrollpane(fitToWidth = true, fitToHeight = true) {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            listmenu(orientation = Orientation.VERTICAL) {
                listMenu = this
                this@ListMenuNavigationPaneFragment.items.forEach {
                    item(it.text, it.graphic, it.tag)
                }
            }
        }
        stackpane {
            contentPane = this
            hgrow = Priority.ALWAYS
        }
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
        val replacement = contentLocator(newValue!!)
        val oldIndex = listMenu.items.indexOf(oldValue)
        val newIndex = listMenu.items.indexOf(newValue)
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
}