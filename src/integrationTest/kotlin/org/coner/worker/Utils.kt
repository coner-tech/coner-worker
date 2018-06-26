package org.coner.worker

import com.google.common.base.Preconditions
import java.nio.file.Files
import java.nio.file.Paths

private val DEPENDENCY_ERROR_MESSAGE_PREFIX = """
Integration test dependency not satisfied. Did you run `./mvnw pre-integration-test`?
""".trim()

class ConerCoreProcessUtils {
    companion object {
        private val VERSION_PROPERTY = "coner-core.version"
        private val VERSION = System.getenv(VERSION_PROPERTY) ?: System.getProperty(VERSION_PROPERTY)

        val PATH_TO_JAR = "it/environment/coner-core-service-$VERSION.jar"
        val PATH_TO_CONFIG = "it/config/coner-core-service.yml"

        init {
            // verify needed environment variable / system property defined
            Preconditions.checkState(
                    System.getenv().containsKey(VERSION_PROPERTY)
                            || System.getProperties().containsKey(VERSION_PROPERTY),
                    "%s not defined as environment variable nor system property",
                    VERSION_PROPERTY
            )

            // verify integration test environmental dependencies are set up
            Preconditions.checkState(
                    Files.exists(Paths.get(PATH_TO_JAR)),
                    "%s jar missing: %s",
                    DEPENDENCY_ERROR_MESSAGE_PREFIX,
                    PATH_TO_JAR
            )
            Preconditions.checkState(
                    Files.exists(Paths.get(PATH_TO_CONFIG)),
                    "%s config missing: %s",
                    DEPENDENCY_ERROR_MESSAGE_PREFIX,
                    PATH_TO_CONFIG
            )
        }
    }
}