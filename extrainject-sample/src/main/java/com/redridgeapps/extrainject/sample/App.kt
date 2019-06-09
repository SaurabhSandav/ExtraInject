package com.redridgeapps.extrainject.sample

import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.redridgeapps.extrainject.sample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()

        setupWorkManager()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this@App).build()
    }

    private fun setupWorkManager() {

        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        WorkManager.initialize(this, config)
    }
}

