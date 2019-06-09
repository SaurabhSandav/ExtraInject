package com.redridgeapps.extrainject.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface InjectableWorker {

    fun create(context: Context, workerParameters: WorkerParameters): ListenableWorker
}
