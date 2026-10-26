package com.softartdev.notedelight.util

import androidx.compose.ui.platform.ClipEntry

actual fun createClipEntry(text: String): ClipEntry = ClipEntry.withPlainText(text)
