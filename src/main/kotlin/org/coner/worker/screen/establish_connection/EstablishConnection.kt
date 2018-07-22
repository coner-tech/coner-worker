package org.coner.worker.screen.establish_connection

import javafx.geometry.Rectangle2D
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesModel
import tornadofx.*

class EstablishConnectionView : View() {

    val controller: EstablishConnectionController by inject()

    override val root = stackpane {
        id = "establish_connection"
        imageview(resources.image("/coner-icon/coner-icon_3840.png")) {
            val heightFactor = 0.645
            fitWidthProperty().bind(primaryStage.widthProperty())
            fitHeightProperty().bind(primaryStage.widthProperty().times(heightFactor))
            viewport = Rectangle2D(804.0, 828.0, 2188.0, 1412.0)
            isPreserveRatio = true
            opacity = 0.1
            isSmooth = true
        }
        vbox {
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
