package com.softartdev.notedelight.feature.console.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.feature.console.ui.buffer.ConsoleBuffer
import com.softartdev.notedelight.feature.console.ui.render.ConsoleHistoryView
import com.softartdev.notedelight.feature.console.ui.render.ConsoleInputRow
import com.softartdev.notedelight.feature.console.ui.theme.ConsoleTheme
import com.softartdev.notedelight.feature.console.ui.theme.rememberConsoleTheme

/**
 * Public terminal surface. Drop-in body for a screen: the caller owns the Scaffold/TopAppBar and
 * any screen-scoped resources (strings, view model), and passes resolved strings into this
 * composable as plain parameters. That keeps this module resource-free while still presenting a
 * fully terminal-like experience.
 *
 * Composition:
 * - A themed [Surface] (no hardcoded black — [ConsoleTheme.surfaceColor] comes from Material3)
 *   wraps the whole terminal and carries the rounded shape + 1.dp outline that gives it a
 *   distinct "panel" appearance in both light and dark themes.
 * - Inside, a single scrolling [Column] contains the read-only [ConsoleHistoryView] followed
 *   directly by the active [ConsoleInputRow]. The two share typography and palette from
 *   [ConsoleTheme] so visually the input is the last line of the transcript. The history
 *   view wraps its [androidx.compose.material3.Text] in a
 *   [androidx.compose.foundation.text.selection.SelectionContainer], so the user can
 *   tap-and-drag to select past commands/output and copy them to the clipboard via the
 *   platform-native affordance.
 * - A transparent tap-gesture detector on the surface focuses the input when the user taps
 *   anywhere in the history — a common terminal affordance. Auto-focus on entry is deliberately
 *   omitted: `requestFocus()` triggers an async `bringIntoView()` that crashes before layout
 *   completes on Android, and `withFrameNanos` (which would defer it past layout) blocks
 *   `waitForIdle()` on wasmJs Chrome Headless in CI.
 * - [Modifier.imePadding] prevents the Android soft keyboard from hiding the input line.
 * - Auto-scroll fires on either (a) the buffer growing (execution appended history) or (b) the
 *   input gaining/losing a `\n` (continuation line added/removed), keeping the caret visible.
 */
@Composable
fun ConsoleSurface(
    buffer: ConsoleBuffer,
    inputText: String,
    running: Boolean,
    runContentDescription: String,
    placeholder: String,
    onInputChange: (String) -> Unit,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme: ConsoleTheme = rememberConsoleTheme()
    val focusRequester: FocusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    // Auto-scroll to the bottom whenever history or multi-line input grows/shrinks.
    val inputNewlineCount: Int = inputText.count { it == '\n' }
    LaunchedEffect(buffer.lineCount, inputNewlineCount, running) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Surface(
        color = theme.surfaceColor,
        border = BorderStroke(width = 1.dp, color = theme.outlineColor),
        shape = theme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .pointerInput(focusRequester) {
                    detectTapGestures(onTap = {
                        runCatching { focusRequester.requestFocus() }
                    })
                },
        ) {
            ConsoleHistoryView(
                buffer = buffer,
                theme = theme,
            )
            ConsoleInputRow(
                inputText = inputText,
                running = running,
                theme = theme,
                focusRequester = focusRequester,
                runContentDescription = runContentDescription,
                placeholder = placeholder,
                onInputChange = onInputChange,
                onExecute = onExecute,
            )
        }
    }
}
