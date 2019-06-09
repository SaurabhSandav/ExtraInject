package com.redridgeapps.extrainject.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

typealias WorkerFactoryMap = Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<InjectableWorker>>

class DaggerWorkerFactory @Inject constructor(
    creators: WorkerFactoryMap
) : WorkerFactory() {

    private val creatorsWithStringKeys = creators.mapKeys { it.key.name }

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        val creator = creatorsWithStringKeys[workerClassName] ?: return null

        return creator.get().create(appContext, workerParameters)
    }
}
