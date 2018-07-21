package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.util.StringConverter
import org.coner.worker.ConnectionPreferences
import org.coner.worker.controller.EasyModeController
import tornadofx.*

class EasyModeConnectionController : Controller() {
    val model: EasyModeConnectionModel by inject()
    val view: EasyModeConnectionView by inject()
    val easyMode: EasyModeController by inject()

    fun useEasyMode() {
        easyMode.start(model.startStepProperty)
    }

    fun onUseEasyModeSuccess() {
        model.connectionPreferences = easyMode.buildConnectionPreferences()
        model.commit()
    }

    fun onUseEasyModeFail(throwable: Throwable) {
        easyMode.stop()
        view.showUseEasyModeError(throwable)
    }
}

class EasyModeConnectionModel : ViewModel() {
    val useEasyModeTaskProperty = SimpleObjectProperty<Task<Unit>>()
    var useEasyModeTask by useEasyModeTaskProperty

    val connectionPreferencesProperty = SimpleObjectProperty<ConnectionPreferences>()
    var connectionPreferences by connectionPreferencesProperty

    val startStepProperty = SimpleObjectProperty<EasyModeController.StartStep>()
    var startStep by startStepProperty
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
                        model.useEasyModeTask = null
                        controller.onUseEasyModeSuccess()
                    } fail {
                        model.useEasyModeTask = null
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
            label(model.startStepProperty, converter = StartStepStringConverter()) {
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

    inner class StartStepStringConverter : StringConverter<EasyModeController.StartStep>() {
        override fun toString(startStep: EasyModeController.StartStep?): String {
            return when (startStep) {
                EasyModeController.StartStep.RESOLVE -> messages["use_easy_mode_progress_step_resolve"]
                EasyModeController.StartStep.START -> messages["use_easy_mode_progress_step_start"]
                EasyModeController.StartStep.HEALTH_CHECK -> messages["use_easy_mode_progress_step_health_check"]
                else -> ""
            }
        }

        override fun fromString(string: String?): EasyModeController.StartStep {
            throw UnsupportedOperationException()
        }

    }
}