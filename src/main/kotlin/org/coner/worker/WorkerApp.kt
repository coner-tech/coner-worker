package org.coner.worker

import com.google.inject.Guice
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import org.coner.worker.screen.MainController
import org.coner.worker.screen.MainView
import tornadofx.*
import kotlin.reflect.KClass

class WorkerApp : App(MainView::class, WorkerStylesheet::class) {

    val guice = Guice.createInjector(AppModule())

    override fun start(stage: Stage) {
        super.start(stage)
        FX.primaryStage.icons.addAll(
                listOf(16, 32, 48, 64, 128, 256, 512, 1024)
                        .map { Image("/coner-icon/coner-icon_$it.png") }
        )
        FX.primaryStage.minWidth = 800.0
        FX.primaryStage.minHeight = 600.0

        stage.setOnCloseRequest(find<MainController>()::onCloseRequest)
    }

    init {
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T {
                return guice.getInstance(type.java)
            }
        }
    }
}

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(WorkerApp::class.java, *args)
}
