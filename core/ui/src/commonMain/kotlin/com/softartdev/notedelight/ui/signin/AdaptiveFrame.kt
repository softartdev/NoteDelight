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
import androidx.window.core.layout.WindowSizeClass

@Composable
fun AdaptiveFrame(
    modifier: Modifier = Modifier,
    mediumMaxWidth: Dp = 520.dp,
    expandedMaxWidth: Dp = 600.dp,
    content: @Composable (PaddingValues) -> Unit
) {
    val info: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    val sizeClass: WindowSizeClass = info.windowSizeClass
    val isAtLeastMedium: Boolean = sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isAtLeastExpanded: Boolean = sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val maxWidth: Dp = when {
        isAtLeastExpanded -> expandedMaxWidth
        isAtLeastMedium -> mediumMaxWidth
        else -> Dp.Unspecified
    }
    val verticalPadding: Dp = if (isAtLeastMedium) 32.dp else 16.dp
    BoxWithConstraints(
        modifier = modifier.fillMaxSize().imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        val availableWidth = Dp(constraints.maxWidth / LocalDensity.current.density)
        val horizontalPadding: Dp = when {
            !isAtLeastMedium -> 24.dp
            maxWidth != Dp.Unspecified && availableWidth > maxWidth -> (availableWidth - maxWidth) / 2f
            else -> 16.dp
        }
        content(PaddingValues(horizontal = horizontalPadding, vertical = verticalPadding))
    }
}
