package com.softartdev.notedelight.feature.console.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

/**
 * Theme-derived palette + typography for the console surface. All values come from
 * [MaterialTheme]; no color is hardcoded so the terminal adapts cleanly to light/dark themes
 * and to user overrides in the Material theme preferences.
 */
@Immutable
data class ConsoleTheme(
    val textStyle: TextStyle,
    val promptColor: Color,
    val continuationPromptColor: Color,
    val commandColor: Color,
    val outputColor: Color,
    val statusColor: Color,
    val errorColor: Color,
    val inputColor: Color,
    val caretColor: Color,
    val selectionColor: Color,
    val surfaceColor: Color,
    val outlineColor: Color,
    val shapes: Shapes,
)

/**
 * Build a [ConsoleTheme] from the current [MaterialTheme]. Recomposes when the color scheme
 * or typography changes (e.g. on theme toggle).
 */
@Composable
fun rememberConsoleTheme(): ConsoleTheme {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
    return remember(colorScheme, typography, shapes) {
        ConsoleTheme(
            textStyle = typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            promptColor = colorScheme.primary,
            continuationPromptColor = colorScheme.secondary,
            commandColor = colorScheme.onSurface,
            outputColor = colorScheme.onSurface,
            statusColor = colorScheme.tertiary,
            errorColor = colorScheme.error,
            inputColor = colorScheme.onSurface,
            caretColor = colorScheme.primary,
            selectionColor = colorScheme.primary.copy(alpha = 0.3f),
            surfaceColor = colorScheme.surface,
            outlineColor = colorScheme.outlineVariant,
            shapes = shapes,
        )
    }
}
