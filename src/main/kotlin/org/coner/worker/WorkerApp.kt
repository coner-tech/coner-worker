package org.coner.worker

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import org.coner.worker.page.MainView
import tornadofx.*

class WorkerApp: App(MainView::class, WorkerStylesheet::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        FX.primaryStage.icons.addAll(
                listOf(16, 32, 48, 64, 128, 256, 512, 1024)
                        .map { Image("/coner-icon/coner-icon_$it.png") }
        )
        FX.primaryStage.minWidth = 512.0
        FX.primaryStage.minHeight = 512.0
    }
}

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(WorkerApp::class.java, *args)
}
