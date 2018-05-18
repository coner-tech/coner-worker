package org.coner.worker.util

import tornadofx.*

interface ConfigPropertiesBoundary<T> {

    fun read(configProperties: ConfigProperties): T

    fun write(value: T, configProperties: ConfigProperties)
}
