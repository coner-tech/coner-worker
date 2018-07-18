package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import org.coner.worker.controller.EasyModeController
import org.coner.worker.controller.MavenController
import tornadofx.*

class EasyModeConnectionController : Controller() {
    val model by inject<EasyModeConnectionModel>()
    val maven by inject<MavenController>()
    val easyMode: EasyModeController by inject()


    fun useEasyMode() {
        easyMode.start()
        easyMode.checkHealth()
    }

    fun onUseEasyModeSuccess() {
        // TODO
    }

    fun onUseEasyModeFail() {
        easyMode.stop()
    }
}

class EasyModeConnectionModel : ViewModel() {
    val useEasyModeTaskProperty = SimpleObjectProperty<Task<Unit>>()
    var useEasyModeTask by useEasyModeTaskProperty
}

class EasyModeConnectionView : View() {
    val model by inject<EasyModeConnectionModel>()
    val controller by inject<EasyModeConnectionController>()

    override val root = stackpane {
        id = "easy_mode"
        useMaxSize = true
        padding = insets(8)
        vbox(spacing = 8) {
            id = "easy_mode_wrapper"
            alignment = Pos.CENTER
            button(messages["use_easy_mode"]) {
                id = "use_easy_mode"
                isDefaultButton = true
                action {
                    model.useEasyModeTask = runAsync {
                        controller.useEasyMode()
                    } success {
                        onUseEasyModeSuccess()
                    } fail {
                        onUseEasyModeFail(it)
                    }
                }
            }
            visibleWhen { model.useEasyModeTaskProperty.isNull }
        }

        vbox(spacing = 8) {
            id = "easy_mode_progress"
            alignment = Pos.CENTER
            progressindicator()
            label(messages["use_easy_mode_progress_text"]) {
                isWrapText = true
            }
            visibleWhen { model.useEasyModeTaskProperty.isNotNull }
        }

    }

    init {
        title = messages["title"]
    }

    override fun onDock() {
        super.onDock()
        model.useEasyModeTask = null
    }

    fun onUseEasyModeSuccess() {
        controller.onUseEasyModeSuccess()
        model.useEasyModeTask = null
    }

    fun onUseEasyModeFail(throwable: Throwable) {
        model.useEasyModeTask = null
        controller.onUseEasyModeFail()
        dialog(
                title = messages["use_easy_mode_error_title"],
                owner = currentWindow
        ) {
            textarea(throwable.toString()) {
                isEditable = false
                isWrapText = true
                prefWidthProperty().bind(this@dialog.widthProperty())
                prefHeightProperty().bind(this@dialog.heightProperty())
            }
        }
    }
}