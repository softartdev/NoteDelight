package com.softartdev.notedelight.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.compose.di.AppModuleImpl
import com.softartdev.notedelight.compose.ui.theme.NoteDelightTheme
import com.softartdev.notedelight.ui.MainRootUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appModule = AppModuleImpl(applicationContext)
        setContent {
            MainRootUI(appModule)
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoteDelightTheme {
        Greeting("Android")
    }
}