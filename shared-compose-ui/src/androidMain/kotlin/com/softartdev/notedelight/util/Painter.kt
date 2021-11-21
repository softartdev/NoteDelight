package com.softartdev.notedelight.util

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@DrawableRes
var appIconId: Int? = null

@Composable
actual fun appIcon(): Painter = painterResource(
    id = requireNotNull(appIconId)
)