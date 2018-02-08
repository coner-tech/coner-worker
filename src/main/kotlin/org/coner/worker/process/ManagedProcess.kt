package org.coner.worker.process

import io.reactivex.Completable

abstract class ManagedProcess {

    abstract fun start(): Completable

    abstract fun stop()

    abstract val started: Boolean

    class FailedToStartException(managedProcess: ManagedProcess) : Exception() {
        override val message: String = "Failed to start process: ${managedProcess::class.simpleName}"
    }
}