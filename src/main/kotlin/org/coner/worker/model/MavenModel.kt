package org.coner.worker.model

import org.eclipse.aether.artifact.Artifact
import tornadofx.*
import java.util.*

class MavenModel : ViewModel() {

    val mavenProperties = ResourceBundle.getBundle("org/coner/worker/maven")

    val artifacts = mutableMapOf<ArtifactKey, Artifact?>().observable()

    sealed class ArtifactKey(val groupId: String, val artifactId: String, val versionProperty: String) {
        object ConerCoreService : ArtifactKey("org.coner", "coner-core-service", "coner-core.version")
    }
}

