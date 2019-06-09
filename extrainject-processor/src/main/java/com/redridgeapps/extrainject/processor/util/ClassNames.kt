package com.redridgeapps.extrainject.processor.util

import com.squareup.javapoet.ClassName

// JavaX
val JAVAX_INJECT: ClassName = ClassName.get("javax.inject", "Inject")
val JAVAX_PROVIDER: ClassName = ClassName.get("javax.inject", "Provider")

// Android Framework
val CONTEXT: ClassName = ClassName.get("android.content", "Context")

// AndroidX annotation
val ANDROIDX_NONNULL: ClassName = ClassName.get("androidx.annotation", "NonNull")

// AndroidX Lifecycle
val VIEWMODEL: ClassName = ClassName.get("androidx.lifecycle", "ViewModel")
val VIEWMODELPROVIDER_FACTORY: ClassName = ClassName.get(
    "androidx.lifecycle",
    "ViewModelProvider",
    "Factory"
)

// WorkManager
val LISTENABLEWORKER: ClassName = ClassName.get("androidx.work", "ListenableWorker")
val WORKERPARAMETERS: ClassName = ClassName.get("androidx.work", "WorkerParameters")

// Dagger
val DAGGER_MODULE: ClassName = ClassName.get("dagger", "Module")
val DAGGER_BINDS: ClassName = ClassName.get("dagger", "Binds")
val DAGGER_INTOMAP: ClassName = ClassName.get("dagger.multibindings", "IntoMap")

// extrainject-work
val WORKERKEY: ClassName = ClassName.get("com.redridgeapps.extrainject.work", "WorkerKey")
val INJECTABLEWORKER: ClassName = ClassName.get("com.redridgeapps.extrainject.work", "InjectableWorker")
