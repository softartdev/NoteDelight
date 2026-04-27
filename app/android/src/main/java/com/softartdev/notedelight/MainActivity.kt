package com.softartdev.notedelight

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.softartdev.notedelight.interactor.BiometricActivityHolder
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val biometricActivityHolder: BiometricActivityHolder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricActivityHolder.attach(this)
        setContent {
            App()
        }
    }

    override fun onDestroy() {
        biometricActivityHolder.detach()
        super.onDestroy()
    }
}
