package com.softartdev.notedelight.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import com.softartdev.notedelight.RootComponent
import com.softartdev.notedelight.ui.MainRootUI
import com.softartdev.notedelight.ui.PreviewMainScreen
import com.softartdev.notedelight.ui.SignInScreenBody
import com.softartdev.notedelight.ui.SplashScreenBody
import com.softartdev.themepref.PreferableMaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = RootComponent(defaultComponentContext())
        setContent {
            MainRootUI(root)
        }
    }
}

@Preview
@Composable
fun DefaultPreview() = PreferableMaterialTheme {
    PreviewMainScreen()
}

@Preview
@Composable
fun PreviewSignInScreen() = PreferableMaterialTheme {
    SignInScreenBody()
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen() = PreferableMaterialTheme {
    SplashScreenBody()
}