package com.redridgeapps.extrainject.sample.di

import androidx.work.WorkerFactory
import com.redridgeapps.extrainject.sample.WorkerFactoryModule
import com.redridgeapps.extrainject.work.DaggerWorkerFactory
import com.redridgeapps.extrainject.work.WorkerFactoryMap
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [WorkerFactoryModule::class])
object AppModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideExampleDependency(): String = "ExampleDependency"

    @Singleton
    @Provides
    @JvmStatic
    fun provideWorkerFactory(workerFactoryMap: WorkerFactoryMap): WorkerFactory = DaggerWorkerFactory(workerFactoryMap)
}
