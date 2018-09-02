package org.coner.worker.di

import com.authzee.kotlinguice4.KotlinModule
import javafx.scene.Node

class PageModule(val navigationMenuParents: Array<Node> = emptyArray()) : KotlinModule() {
    override fun configure() {
        bind<Array<Node>>().toInstance(navigationMenuParents)
    }
}