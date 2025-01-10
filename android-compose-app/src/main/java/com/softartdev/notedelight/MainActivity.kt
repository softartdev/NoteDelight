package com.softartdev.notedelight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.softartdev.notedelight.navigation.Router
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val router: Router by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(router)
        }
    }
}
