package com.softartdev.notedelight.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.softartdev.annotation.Preview
import com.softartdev.mr.painterResource
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.shared.presentation.splash.SplashResult
import com.softartdev.notedelight.shared.presentation.splash.SplashViewModel
import com.softartdev.notedelight.ui.dialog.DialogHolder

@Composable
fun SplashScreen(splashViewModel: SplashViewModel, navSignIn: () -> Unit, navMain: () -> Unit) {
    val splashResultState: State<SplashResult> = splashViewModel.resultStateFlow.collectAsState()
    DisposableEffect(splashViewModel) {
        splashViewModel.checkEncryption()
        onDispose(splashViewModel::onCleared)
    }
    val dialogHolder: DialogHolder = remember { DialogHolder() }
    when (val splashResult: SplashResult = splashResultState.value) {
        is SplashResult.Loading -> Unit//TODO: progress bar
        is SplashResult.NavSignIn -> navSignIn()
        is SplashResult.NavMain -> navMain()
        is SplashResult.ShowError -> dialogHolder.showError(splashResult.message)
    }
    SplashScreenBody(showDialogIfNeed = dialogHolder.showDialogIfNeed)
}

@Composable
fun SplashScreenBody(
    showDialogIfNeed: @Composable () -> Unit = {},
) = Box(modifier = Modifier.fillMaxSize()) {
    Image(
        painter = painterResource(MR.images.app_icon),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.Center)
            .background(color = MaterialTheme.colors.background)
    )
    showDialogIfNeed()
}

@Preview
@Composable
fun PreviewSplashScreen() = SplashScreenBody()