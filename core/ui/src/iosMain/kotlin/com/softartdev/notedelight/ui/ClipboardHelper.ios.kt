package com.softartdev.notedelight.ui

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun createClipEntry(text: String): ClipEntry = ClipEntry.withPlainText(text)
