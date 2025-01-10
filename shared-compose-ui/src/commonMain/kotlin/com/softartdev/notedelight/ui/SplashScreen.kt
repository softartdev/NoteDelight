package com.softartdev.notedelight.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.splash.SplashViewModel
import com.softartdev.notedelight.ui.icon.FileLock

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel,
) {
    val showLoading: Boolean by splashViewModel.stateFlow.collectAsState()
    LaunchedEffect(splashViewModel) {
        splashViewModel.checkEncryption()
    }
    SplashScreenBody(showLoading)
}

@Composable
fun SplashScreenBody(showLoading: Boolean = false) = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background)
) {
    Icon(
        modifier = Modifier
            .align(Alignment.Center)
            .size(192.dp),
        imageVector = Icons.Filled.FileLock,
        contentDescription = null,
        tint = Color.Cyan,
    )
    if (showLoading) {
        LinearProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() = SplashScreenBody(true)
