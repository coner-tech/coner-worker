package org.coner.worker.page

import javafx.scene.Node
import org.testfx.api.FxRobot
import javax.inject.Inject

class EasyModeConnectionPage @Inject constructor(
        val robot: FxRobot
) {

    val root: Node = robot.lookup("#easy_mode").query()

}