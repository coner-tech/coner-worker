package org.coner.worker.model

import tornadofx.*

class ConerWorkerPropertiesModel : ViewModel() {
    val conerCoreVersion = messages["coner-core.version"]

    init {
        if (messages.keySet().isEmpty()) throw RuntimeException(
                "Messages is empty. You need to run maven goal: generate-resources."
        )
        /*
        If you're running Coner Worker from IntelliJ, you need to edit your run configuration for starting it
        to perform the following steps before launch:
          - Build
          - Run Maven Goal generate-resources
         */
    }
}