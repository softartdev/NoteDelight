package com.softartdev.notedelight.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual fun createClipEntry(text: String): ClipEntry = ClipEntry(StringSelection(text))
