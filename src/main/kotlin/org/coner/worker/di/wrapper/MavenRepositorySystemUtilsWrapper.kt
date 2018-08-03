package org.coner.worker.di.wrapper

import org.apache.maven.repository.internal.MavenRepositorySystemUtils

class MavenRepositorySystemUtilsWrapper {
    fun newSession() = MavenRepositorySystemUtils.newSession()
}