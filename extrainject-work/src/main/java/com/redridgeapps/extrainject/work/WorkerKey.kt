package com.redridgeapps.extrainject.work

import androidx.work.ListenableWorker
import dagger.MapKey
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
