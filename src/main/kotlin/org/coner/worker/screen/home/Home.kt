package org.coner.worker.screen.home

import org.coner.worker.widget.ListMenuNavigationFragment
import tornadofx.*

class HomeView : View() {

    private val listMenuNavParams = mapOf(
            ListMenuNavigationFragment::adapter to ListMenuNavigationFragment.Adapter(
                    count = 2,
                    locator = this::locate
            )
    )

    lateinit var listMenuNav: ListMenuNavigationFragment

    override val root = stackpane {
        id = "home"
        add(find<ListMenuNavigationFragment>(listMenuNavParams) {
            listMenuNav = this
        })
    }

    init {
        title = messages["title"]
    }

    private fun locate(index: Int) = when (index) {
        0 -> find<EventsView>()
        1 -> find<SeasonsView>()
        else -> throw IllegalArgumentException()
    }
}