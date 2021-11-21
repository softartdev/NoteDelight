package com.softartdev.notedelight.util

import androidx.compose.runtime.Composable

@Composable
expect fun AlertDialog(
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit
)

@Composable
expect fun AlertDialog(
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    buttons: @Composable () -> Unit,
    onDismissRequest: () -> Unit
)