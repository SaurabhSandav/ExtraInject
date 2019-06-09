package com.redridgeapps.extrainject.sample

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.redridgeapps.extrainject.annotations.WorkerInject

class MainWorker @WorkerInject constructor(
    context: Context,
    workerParameters: WorkerParameters,
    private val testProvided: String
) : Worker(context, workerParameters) {

    override fun doWork(): Result {

        val msg = """
            Worker Injection Successful!
            testProvided -> $testProvided
        """.trimIndent()

        Log.d(javaClass.simpleName, msg)
        return Result.success()
    }

    companion object {

        fun scheduleNow(appContext: Context) {

            val mainWork = OneTimeWorkRequestBuilder<MainWorker>().build()

            WorkManager.getInstance(appContext).enqueue(mainWork)
        }
    }
}
