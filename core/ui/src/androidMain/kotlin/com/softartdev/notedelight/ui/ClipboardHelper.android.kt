package com.softartdev.notedelight.ui

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun createClipEntry(text: String): ClipEntry = ClipEntry(ClipData.newPlainText("text", text))
