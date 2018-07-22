package org.coner.worker.page

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import org.coner.worker.util.testfx.lookupAndQuery
import org.testfx.api.FxRobot
import javax.inject.Inject

class EasyModeConnectionPage @Inject constructor(
        val robot: FxRobot
) {

    val root: Node = robot.lookupAndQuery("#easy_mode_connection")
    val useWrapper: Node = robot.lookupAndQuery("#use_wrapper")
    val useButton: Button = robot.from(useWrapper).lookupAndQuery("#button")
    val progressWrapper: Node = robot.from(root).lookupAndQuery("#progress_wrapper")
    val progressIndicator: ProgressIndicator = robot.from(progressWrapper).lookupAndQuery("#indicator")
    val progressLabel: Label = robot.from(progressWrapper).lookupAndQuery("#label")
    val progressStartStep: Label = robot.from(progressWrapper).lookupAndQuery("#start_step")

    val useNodes = arrayOf(useWrapper, useButton)
    val progressNodes = arrayOf(progressWrapper, progressIndicator, progressLabel, progressStartStep)

    fun clickUseButton() {
        robot.clickOn(useButton)
    }

}