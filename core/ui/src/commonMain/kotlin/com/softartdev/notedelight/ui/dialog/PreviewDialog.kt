package com.softartdev.notedelight.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun PreviewDialog(dialogContent: @Composable () -> Unit) = Box(
    modifier = Modifier.fillMaxSize(),
    content = { dialogContent() }
)
