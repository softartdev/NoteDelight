package com.softartdev.notedelight.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import com.softartdev.notedelight.interactor.SnackbarInteractor
import kotlinx.coroutines.CoroutineScope

@Composable
fun GlobalSnackbarHost(
    modifier: Modifier = Modifier,
    snackbarInteractor: SnackbarInteractor,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    DisposableEffect(key1 = snackbarInteractor, key2 = snackbarHostState, key3 = clipboardManager) {
        snackbarInteractor.setDependencies(snackbarHostState, clipboardManager, coroutineScope)
        onDispose(snackbarInteractor::releaseDependencies)
    }
    SnackbarHost(hostState = snackbarHostState, modifier = modifier)
}
