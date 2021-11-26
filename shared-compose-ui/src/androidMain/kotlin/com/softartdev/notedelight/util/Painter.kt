package com.softartdev.notedelight.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.softartdev.notedelight.shared.compose.R

@Composable
actual fun appIcon(): Painter = painterResource(
    id = R.drawable.app_icon
)