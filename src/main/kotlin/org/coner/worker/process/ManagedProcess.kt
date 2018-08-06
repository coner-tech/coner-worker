package org.coner.worker.process

abstract class ManagedProcess {

    abstract fun start()

    abstract fun stop()

    abstract val started: Boolean

    class FailedToStartException(managedProcess: ManagedProcess) : Exception() {
        override val message: String = "Failed to start process: ${managedProcess::class.simpleName}"
    }
}