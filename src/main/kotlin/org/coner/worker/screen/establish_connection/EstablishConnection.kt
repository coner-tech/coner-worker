package org.coner.worker.screen.establish_connection

import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.WorkerStylesheet
import tornadofx.*

class EstablishConnectionView : View() {

    val controller: EstablishConnectionController by inject()

    override val root = vbox {
        id = "establish_connection"
        label(titleProperty) {
            id = "title"
            addClass(WorkerStylesheet.h1)
        }
        tabpane {
            id = "tabs"
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            vgrow = Priority.ALWAYS
            tab(find<EasyModeConnectionView>()) {
                id = "easy-mode-tab"
            }
            tab(find(CustomConnectionView::class)) {
                id = "custom-connection-tab"
            }
        }
    }

    init {
        title = messages["title"]
        controller.noOp()
    }

    override fun onDock() {
        super.onDock()
        controller.startListeningForConnectionPreferences()

    }

    override fun onUndock() {
        super.onUndock()
        controller.stopListeningForConnectionPreferences()
    }
}

class EstablishConnectionController : Controller() {

    val connectionPreferencesModel: ConnectionPreferencesModel by inject()
    val easyModeConnectionModel: EasyModeConnectionModel by inject()
    val customConnectionModel: CustomConnectionModel by inject()

    fun noOp() {
        // no-op
        // needed to guarantee controller init
    }

    fun startListeningForConnectionPreferences() {
        easyModeConnectionModel.connectionPreferencesProperty.addListener(onConnectionPreferencesChanged)
        customConnectionModel.connectionPreferencesProperty.addListener(onConnectionPreferencesChanged)
    }

    fun stopListeningForConnectionPreferences() {
        easyModeConnectionModel.connectionPreferencesProperty.removeListener(onConnectionPreferencesChanged)
        customConnectionModel.connectionPreferencesProperty.removeListener(onConnectionPreferencesChanged)
    }

    val onConnectionPreferencesChanged = ChangeListener<ConnectionPreferences> { observable, oldValue, newValue ->
        connectionPreferencesModel.item = newValue
    }
}
