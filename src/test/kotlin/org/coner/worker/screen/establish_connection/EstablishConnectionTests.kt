package org.coner.worker.screen.establish_connection

import com.authzee.kotlinguice4.getInstance
import com.google.inject.Guice
import com.google.inject.Injector
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.coner.worker.ConnectionPreferences
import org.coner.worker.ConnectionPreferencesController
import org.coner.worker.ConnectionPreferencesModel
import org.coner.worker.di.PageModule
import org.coner.worker.page.EstablishConnectionPage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions
import tornadofx.*
import java.util.concurrent.CountDownLatch

class EstablishConnectionViewTest {

    lateinit var view: EstablishConnectionView
    lateinit var page: EstablishConnectionPage

    lateinit var injector: Injector

    @Before
    fun before() {
        val stage = FxToolkit.registerPrimaryStage()
        stage.width = 600.0
        val app = App(EstablishConnectionView::class)
        with(app.scope) {
            set(mockk<EstablishConnectionController>(relaxed = true))
        }
        FxToolkit.setupApplication { app }
        view = stage.uiComponent()!!
        injector = Guice.createInjector(PageModule(
                navigationMenuParents = arrayOf(view.root)
        ))
        FX.runAndWait {
            page = injector.getInstance()
        }
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(view.app)
    }

    @Test
    fun onInitItShouldCallControllerNoOp() {
        // depends on view init during before()
        verify { view.controller.noOp() }
    }

    @Test
    fun itShouldHaveEasyModeNav() {
        page.clickEasyModeNav()

        Assertions.assertThat(view.listMenuNav.activeItemIndex).isEqualTo(0)
    }

    @Test
    fun itShouldHaveCustomConnectionNav() {
        page.clickCustomConnectionNav()

        Assertions.assertThat(view.listMenuNav.activeItemIndex).isEqualTo(1)
    }

    @Test
    fun itShouldTraversePages() {
        page.clickCustomConnectionNav()
        page.robot.clickOn(page.customPage.adminPort)
        page.clickEasyModeNav()
    }
}

class EstablishConnectionControllerTest {

    lateinit var controller: EstablishConnectionController

    lateinit var connectionPreferencesModel: ConnectionPreferencesModel

    @Before
    fun before() {
        val app = App()
        with(app.scope) {
            set(mockk<EasyModeConnectionController>(relaxed = true))
            set(mockk<CustomConnectionController>(relaxed = true))
            set(mockk<EstablishConnectionView>(relaxed = true))
            set(mockk<ConnectionPreferencesController>(relaxed = true))
        }
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication { app }
        controller = find()
        connectionPreferencesModel = find()
        val connectionPreferencesController: ConnectionPreferencesController = find()
        every { connectionPreferencesController.model }.returns(connectionPreferencesModel)
    }

    @After
    fun after() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(controller.app)
    }

    @Test
    fun itShouldResolveSpecificConnectionControllers() {
        assertThat(controller.specificConnectionControllers)
                .hasSize(2)
                .containsExactly(
                        controller.easyModeConnectionController,
                        controller.customConnectionController
                )
    }

    @Test
    fun itShouldNotOfferConnectionPreferencesWhenNull() {
        connectionPreferencesModel.item = null

        controller.offerConnectionPreferencesToSpecificEstablishConnectionControllers()

        controller.specificConnectionControllers.forEach {
            verify(exactly = 0) { it.offer(any()) }
        }
    }

    @Test
    fun itShouldOfferConnectionPreferencesToEveryControllerWhenIgnored() {
        val connectionPreferences = ConnectionPreferences()
        connectionPreferencesModel.item = connectionPreferences

        controller.offerConnectionPreferencesToSpecificEstablishConnectionControllers()

        controller.specificConnectionControllers.forEach {
            verify(exactly = 1) { it.offer(connectionPreferences) }
        }
    }

    @Test
    fun itShouldConnectWhenOfferClaimedAndSaved() {
        val connectionPreferences = ConnectionPreferences().apply {
            saved = true
        }
        connectionPreferencesModel.item = connectionPreferences
        every { controller.easyModeConnectionController.offer(connectionPreferences) }
                .returns(SpecificEstablishConnectionController.OfferResult.Ignored())
        every { controller.customConnectionController.offer(connectionPreferences) }
                .returns(SpecificEstablishConnectionController.OfferResult.Claimed())
        val latch = CountDownLatch(1)
        every { controller.customConnectionController.connect(connectionPreferences) }
                .answers { latch.countDown() }

        controller.offerConnectionPreferencesToSpecificEstablishConnectionControllers()

        latch.await()
        verify { controller.view.navigateTo(1) }
        verify { controller.customConnectionController.connect(connectionPreferences) }
    }
}