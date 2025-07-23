package com.softartdev.notedelight.ui.dialog

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Preview
@Composable
fun PreviewDialog(dialogContent: @Composable () -> Unit) = Box(
    modifier = Modifier.fillMaxSize(),
    content = { dialogContent() }
)
