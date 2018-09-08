package org.coner.worker.util.javafx.scene.control

import javafx.scene.Node
import javafx.scene.control.ScrollPane

/**
 * Because TestFX currently doesn't handle scrolling ScrollView to a child automatically (see
 * https://github.com/TestFX/TestFX/issues/40), this extension method takes the fangs off that limitation.
 *
 * Inspired by:
 * https://stackoverflow.com/questions/15840513/javafx-scrollpane-programmatically-moving-the-viewport-centering-content/23518314#23518314
 */
fun ScrollPane.scrollToChild(child: Node) {
//    check(childrenUnmodifiable.contains(child)) { "Node passed as child is not a direct child of this ScrollPane" }
    val h = content.boundsInLocal.height
    val y = (child.boundsInParent.maxY + child.boundsInParent.minY) / 2.0
    val v = viewportBounds.height
    vvalue = vmax * ((y - 0.5 * v) / (h - v))
}