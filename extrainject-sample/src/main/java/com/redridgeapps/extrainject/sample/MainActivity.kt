package com.redridgeapps.extrainject.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory

    private val viewModel by viewModels<MainViewModel> { viewModelFactory.with(20) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(javaClass.simpleName, viewModel.test())

        MainWorker.scheduleNow(applicationContext)
    }
}
