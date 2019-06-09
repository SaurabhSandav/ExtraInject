package com.redridgeapps.extrainject.sample.di

import com.redridgeapps.extrainject.sample.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AndroidComponentBuilder {

    // Activities

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}
