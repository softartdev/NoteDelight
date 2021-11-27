package com.softartdev.notedelight.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.mr.mokoResourcesContext
import com.softartdev.notedelight.compose.ui.theme.NoteDelightTheme
import com.softartdev.notedelight.ui.MainRootUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mokoResourcesContext = applicationContext
        setContent {
            MainRootUI()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoteDelightTheme {
        MainRootUI()
    }
}