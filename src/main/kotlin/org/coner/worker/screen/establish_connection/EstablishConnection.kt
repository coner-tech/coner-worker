package org.coner.worker.screen.establish_connection

import javafx.geometry.Rectangle2D
import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesController
import org.coner.worker.widget.ListMenuNavigationFragment
import tornadofx.*

class EstablishConnectionView : View() {

    val controller: EstablishConnectionController by inject()

    private val listMenuNavParams = mapOf(
            ListMenuNavigationFragment::adapter to ListMenuNavigationFragment.Adapter(
                    count = 2,
                    locator = this::locate
            )
    )
    lateinit var listMenuNav: ListMenuNavigationFragment

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
        add(find<ListMenuNavigationFragment>(listMenuNavParams) { listMenuNav = this })
    }

    init {
        title = messages["title"]
        controller.noOp()
    }

    private fun locate(index: Int) = when (index) {
        0 -> find<EasyModeConnectionView>()
        1 -> find<CustomConnectionView>()
        else -> throw IllegalArgumentException()
    }

    fun navigateTo(index: Int) {
        find<ListMenuNavigationFragment>(listMenuNavParams).navigateTo(index)
    }

    override fun onDock() {
        super.onDock()
        controller.startListeningForConnectionPreferences()
        controller.offerConnectionPreferencesToSpecificEstablishConnectionControllers()

    }

    override fun onUndock() {
        super.onUndock()
        controller.stopListeningForConnectionPreferences()
    }
}

class EstablishConnectionController : Controller() {

    val connectionPreferencesController: ConnectionPreferencesController by inject()
    val easyModeConnectionController: EasyModeConnectionController by inject()
    val easyModeConnectionModel: EasyModeConnectionModel by inject()
    val customConnectionController: CustomConnectionController by inject()
    val customConnectionModel: CustomConnectionModel by inject()
    val view: EstablishConnectionView by inject()

    val specificConnectionControllers by lazy {
        arrayOf<SpecificEstablishConnectionController>(
                easyModeConnectionController,
                customConnectionController
        )
    }

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

    fun offerConnectionPreferencesToSpecificEstablishConnectionControllers() {
        val connectionPreferences = connectionPreferencesController.model.item ?: return
        for ((index, controller) in specificConnectionControllers.withIndex()) {
            val result = controller.offer(connectionPreferences)
            if (result is SpecificEstablishConnectionController.OfferResult.Claimed) {
                runLater {
                    view.navigateTo(index)
                    if (connectionPreferences.saved) {
                        controller.connect(connectionPreferences)
                    }
                }
                break
            }
        }
    }

    val onConnectionPreferencesChanged = ChangeListener<ConnectionPreferences> { observable, oldValue, newValue ->
        connectionPreferencesController.model.item = newValue
    }
}

interface SpecificEstablishConnectionController {
    /**
     * Offer connection preferences to a specific controller. Implementations will inspect the argument and decide
     * whether to claim or ignore it. When a specific controller claims a connection preference, it should update its view
     * accordingly. The parent view will navigate to the view associated with the specific controller.
     */
    fun offer(connectionPreferences: ConnectionPreferences): OfferResult

    sealed class OfferResult {
        class Claimed : OfferResult()
        class Ignored : OfferResult()
    }

    /**
     * Specific controllers will receive this call when they have claimed a saved connection preference
     */
    fun connect(connectionPreferences: ConnectionPreferences)
}