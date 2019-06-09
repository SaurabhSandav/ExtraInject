package com.redridgeapps.extrainject.sample

import androidx.lifecycle.ViewModel
import com.redridgeapps.extrainject.annotations.Extra
import com.redridgeapps.extrainject.annotations.ViewModelInject

class MainViewModel @ViewModelInject constructor(
    @Extra val testExtra: Int,
    private val testProvided: String
) : ViewModel() {

    fun test(): String {
        return """
            ViewModel Injection Successful!
            testExtra -> $testExtra
            testProvided -> $testProvided
        """.trimIndent()
    }
}
