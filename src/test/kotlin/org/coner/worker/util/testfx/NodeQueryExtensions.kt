package org.coner.worker.util.testfx

import javafx.scene.Node
import org.testfx.service.query.NodeQuery


inline fun <reified T : Node> NodeQuery.lookupAndQuery(query: String): T {
    return lookup(query).query()
}