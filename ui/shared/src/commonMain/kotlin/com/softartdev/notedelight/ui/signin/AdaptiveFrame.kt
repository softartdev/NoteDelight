package com.softartdev.notedelight.ui.signin

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun AdaptiveFrame(
    modifier: Modifier = Modifier,
    mediumMaxWidth: Dp = 520.dp,
    expandedMaxWidth: Dp = 600.dp,
    content: @Composable (PaddingValues) -> Unit
) {
    val info: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    val widthClass: WindowWidthSizeClass = info.windowSizeClass.windowWidthSizeClass

    val maxWidth: Dp = when (widthClass) {
        WindowWidthSizeClass.COMPACT -> Dp.Unspecified
        WindowWidthSizeClass.MEDIUM -> mediumMaxWidth
        else -> expandedMaxWidth
    }
    val verticalPadding: Dp = when (widthClass) {
        WindowWidthSizeClass.COMPACT -> 16.dp
        else -> 32.dp
    }
    BoxWithConstraints(
        modifier = modifier.fillMaxSize().imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        val availableWidth = Dp(constraints.maxWidth / LocalDensity.current.density)
        val horizontalPadding: Dp = when (widthClass) {
            WindowWidthSizeClass.COMPACT -> 24.dp
            else -> when {
                maxWidth != Dp.Unspecified && availableWidth > maxWidth -> (availableWidth - maxWidth) / 2f
                else -> 16.dp
            }
        }
        content(PaddingValues(horizontal = horizontalPadding, vertical = verticalPadding))
    }
}
