package com.softartdev.notedelight.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.ui.main.PreviewMainScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun GlobalSnackbarHost(
    modifier: Modifier = Modifier,
    snackbarInteractor: SnackbarInteractor,
    snackbarHostState: SnackbarHostState,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    DisposableEffect(key1 = snackbarInteractor, key2 = snackbarHostState, key3 = clipboardManager) {
        snackbarInteractor.setDependencies(snackbarHostState, clipboardManager, coroutineScope)
        onDispose(snackbarInteractor::releaseDependencies)
    }
    SnackbarHost(
        modifier = modifier,
        snackbar = {
            Snackbar(
                modifier = Modifier.imePadding().navigationBarsPadding(),
                snackbarData = it
            )
        },
        hostState = snackbarHostState
    )
}

@Preview
@Composable
fun GlobalSnackbarHostPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(message = "This is a preview snackbar message.")
        }
        PreviewMainScreen(snackbarHostState = snackbarHostState)
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }
}
