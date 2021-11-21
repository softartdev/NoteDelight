package com.softartdev.notedelight.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
actual fun appIcon(): Painter = painterResource(resourcePath = "app_icon.png")