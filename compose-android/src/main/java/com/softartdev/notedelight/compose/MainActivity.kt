package com.softartdev.notedelight.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.mr.mokoResourcesContext
import com.softartdev.notedelight.compose.di.AppModuleImpl
import com.softartdev.notedelight.compose.ui.theme.NoteDelightTheme
import com.softartdev.notedelight.ui.MainRootUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mokoResourcesContext = applicationContext
        val appModule = AppModuleImpl(applicationContext)
        setContent {
            MainRootUI(appModule)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val appModule = AppModuleImpl(LocalContext.current)
    NoteDelightTheme {
        MainRootUI(appModule)
    }
}