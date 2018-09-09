package org.coner.worker.screen.establish_connection

import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.util.StringConverter
import org.coner.worker.ConnectionPreferences
import org.coner.worker.controller.EasyModeController
import tornadofx.*
import java.util.logging.Level

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
        id = "easy_mode_connection"
        useMaxSize = true
        padding = insets(8)
        vbox(spacing = 8) {
            id = "use_wrapper"
            alignment = Pos.CENTER
            button(messages["use_easy_mode"]) {
                id = "use_easy_mode_button"
                isDefaultButton = true
                action {
                    println("use easy mode button action")
                    model.useEasyModeTask = runAsync {
                        println("calling controller.useEasyMode()")
                        controller.useEasyMode()
                        println("done with controller.useEasyMode()")
                    } success {
                        model.useEasyModeTask = null
                        println("calling controller.onUseEasyModeSuccess")
                        controller.onUseEasyModeSuccess()
                    } fail {
                        log.log(Level.SEVERE, "failed to use easy mode", it)
                        model.useEasyModeTask = null
                        println("calling controller.onUseEasyModeFail(it)")
                        controller.onUseEasyModeFail(it)
                    }
                }
            }
            visibleWhen { model.useEasyModeTaskProperty.isNull }
        }

        vbox(spacing = 8) {
            id = "progress_wrapper"
            alignment = Pos.CENTER
            progressindicator() {
                id = "indicator"
            }
            label(messages["use_easy_mode_progress_text"]) {
                id = "label"
                isWrapText = true
            }
            label(model.startStepProperty, converter = StartStepStringConverter()) {
                id = "start_step"
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