package com.softartdev.notedelight.util

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

expect fun createClipEntry(text: String): ClipEntry

suspend fun Clipboard.setText(text: String) {
    setClipEntry(createClipEntry(text))
}
