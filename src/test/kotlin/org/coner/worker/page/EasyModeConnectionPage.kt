package org.coner.worker.page

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import org.coner.worker.util.testfx.lookupAndQuery
import org.testfx.api.FxRobot
import javax.inject.Inject

class EasyModeConnectionPage @Inject constructor(
        val robot: FxRobot
) {

    val root by lazy { robot.lookupAndQuery<Node>("#easy_mode_connection") }
    val useWrapper by lazy { robot.lookupAndQuery<Node>("#use_wrapper") }
    val useButton by lazy {
        println(">>> lazy init useButton")
        val button = robot.from(useWrapper).lookup("#use_easy_mode_button").queryButton()
        println("<<< lazy init useButton")
        button
    }
    val progressWrapper by lazy { robot.from(root).lookupAndQuery<Node>("#progress_wrapper") }
    val progressIndicator by lazy { robot.from(progressWrapper).lookupAndQuery<ProgressIndicator>("#indicator") }
    val progressLabel by lazy { robot.from(progressWrapper).lookupAndQuery<Label>("#label") }
    val progressStartStep by lazy { robot.from(progressWrapper).lookupAndQuery<Label>("#start_step") }

    val useNodes = arrayOf(useWrapper, useButton)
    val progressNodes = arrayOf(progressWrapper, progressIndicator, progressLabel, progressStartStep)

    fun clickUseButton() {
        println(">>> clickUseButton()")
        robot.clickOn(useButton)
        println("<<< clickUseButton()")
    }

}