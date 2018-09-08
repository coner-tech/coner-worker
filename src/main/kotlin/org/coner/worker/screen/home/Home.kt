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

    override val root = stackpane {
        add(find<ListMenuNavigationFragment>(listMenuNavParams))
    }

    init {
        title = messages["title"]
    }

    private fun locate(index: Int) = when(index) {
        0 -> find<EventsView>()
        1 -> find<SeasonsView>()
        else -> throw IllegalArgumentException()
    }
}