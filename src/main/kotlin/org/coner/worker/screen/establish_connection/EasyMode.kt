package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import org.coner.worker.ConnectionPreferences
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
        model.useEasyModeTask = null
        model.connectionPreferences = easyMode.buildConnectionPreferences()
        model.commit()
    }

    fun onUseEasyModeFail(throwable: Throwable) {
        easyMode.stop()
        model.useEasyModeTask = null
        find<EasyModeConnectionView>().showUseEasyModeError(throwable)
    }
}

class EasyModeConnectionModel : ViewModel() {
    val useEasyModeTaskProperty = SimpleObjectProperty<Task<Unit>>()
    var useEasyModeTask by useEasyModeTaskProperty

    val connectionPreferencesProperty = SimpleObjectProperty<ConnectionPreferences>()
    var connectionPreferences by connectionPreferencesProperty

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
                        controller.onUseEasyModeSuccess()
                    } fail {
                        controller.onUseEasyModeFail(it)
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

    fun showUseEasyModeError(throwable: Throwable) {
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