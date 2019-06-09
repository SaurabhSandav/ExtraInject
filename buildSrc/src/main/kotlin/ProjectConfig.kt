object Android {
    private const val GRADLE_PLUGIN_VERSION = "3.6.0-alpha03"

    const val GRADLE_PLUGIN = "com.android.tools.build:gradle:$GRADLE_PLUGIN_VERSION"
}

object Kotlin {
    private const val VERSION = "1.3.31"

    const val GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
    const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$VERSION"
}

object Jetpack {
    private const val ACTIVITY_KTX_VERSION = "1.0.0-alpha08"
    private const val APPCOMPAT_VERSION = "1.1.0-alpha05"
    private const val CORE_KTX_VERSION = "1.1.0-beta01"

    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:$ACTIVITY_KTX_VERSION"
    const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
    const val CORE_KTX = "androidx.core:core-ktx:$CORE_KTX_VERSION"
}

object Lifecycle {
    private const val VERSION = "2.2.0-alpha01"

    const val EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:$VERSION"
    const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
    const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    const val COMMON_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$VERSION"
}

object WorkManager {
    private const val VERSION = "2.1.0-alpha02"

    const val RUNTIME_KTX = "androidx.work:work-runtime-ktx:$VERSION"
}

object Dagger {
    private const val VERSION = "2.23"

    const val DAGGER = "com.google.dagger:dagger:$VERSION"
    const val ANDROID_SUPPORT = "com.google.dagger:dagger-android-support:$VERSION"
    const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"
    const val ANDROID_PROCESSOR = "com.google.dagger:dagger-android-processor:$VERSION"
}

object JavaPoet {
    private const val VERSION = "1.11.1"

    const val JAVA_POET = "com.squareup:javapoet:$VERSION"
}

object Auto {
    private const val COMMON_VERSION = "0.1"
    private const val SERVICE_VERSION = "1.0-rc5"

    const val COMMON = "com.google.auto:auto-common:$COMMON_VERSION"
    const val SERVICE = "com.google.auto.service:auto-service:$SERVICE_VERSION"
}
