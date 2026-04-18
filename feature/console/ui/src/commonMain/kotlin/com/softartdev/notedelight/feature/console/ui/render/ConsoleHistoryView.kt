package com.softartdev.notedelight.feature.console.ui.render

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.softartdev.notedelight.feature.console.ui.CONSOLE_TRANSCRIPT_TAG
import com.softartdev.notedelight.feature.console.ui.buffer.ConsoleBuffer
import com.softartdev.notedelight.feature.console.ui.buffer.ConsoleBufferLine
import com.softartdev.notedelight.feature.console.ui.buffer.ConsoleSegmentRole
import com.softartdev.notedelight.feature.console.ui.theme.ConsoleTheme

/**
 * Read-only scrollback view. The whole buffer is flattened into a single [AnnotatedString]
 * with per-segment color spans derived from [ConsoleTheme] and rendered by one [Text]
 * composable inside a [SelectionContainer]. The container wires native tap-and-drag
 * selection on every target — Android, iOS, desktop, wasmJs — so the user can copy
 * transcript output to the clipboard via the platform-native Copy affordance (long-press
 * menu on touch, `Ctrl/Cmd+C` on desktop, context menu in the browser).
 *
 * The previous implementation painted each line into a [androidx.compose.foundation.Canvas]
 * with a shared `TextMeasurer`. Canvas-drawn glyphs are opaque to Compose's selection
 * system, so selection required custom hit-testing. Delegating to `Text` instead gives us
 * native selection for free; semantics (`SemanticsProperties.Text`) are populated by `Text`
 * itself, so existing `hasText(...)` and `hasTestTag(...)` matchers continue to work.
 */
@Composable
internal fun ConsoleHistoryView(
    buffer: ConsoleBuffer,
    theme: ConsoleTheme,
    modifier: Modifier = Modifier,
) {
    if (buffer.isEmpty) {
        // No history yet; still reserve the tag so tests/a11y can query the (empty) transcript.
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .testTag(CONSOLE_TRANSCRIPT_TAG)
                .semantics {
                    contentDescription = ""
                    this[SemanticsProperties.Text] = emptyList()
                },
        )
        return
    }
    val annotatedText: AnnotatedString = remember(buffer, theme) {
        buildAnnotatedString {
            buffer.lines.forEachIndexed { index, line ->
                appendBufferLine(line = line, theme = theme)
                if (index != buffer.lines.lastIndex) append('\n')
            }
        }
    }
    val plainText: String = remember(buffer) { buffer.plainText() }
    // Expose one AnnotatedString per buffer line under SemanticsProperties.Text. The default
    // `Text` semantics would collapse the whole transcript into one entry, which breaks
    // exact-match `hasText("0")` style queries in the feature-test suite (a single-line result
    // like "0" would no longer match). Publishing per-line entries preserves that contract and
    // still lets `SelectionContainer` + the underlying text layout drive clipboard selection.
    val lineTexts: List<AnnotatedString> = remember(buffer) {
        buffer.lines.map { AnnotatedString(it.rawText) }
    }
    SelectionContainer(modifier = modifier.fillMaxWidth()) {
        Text(
            text = annotatedText,
            style = theme.textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(CONSOLE_TRANSCRIPT_TAG)
                .semantics {
                    contentDescription = plainText
                    this[SemanticsProperties.Text] = lineTexts
                },
        )
    }
}

private fun AnnotatedString.Builder.appendBufferLine(
    line: ConsoleBufferLine,
    theme: ConsoleTheme,
) {
    for (segment in line.segments) {
        val color = when (segment.role) {
            ConsoleSegmentRole.PROMPT -> theme.promptColor
            ConsoleSegmentRole.CONTINUATION_PROMPT -> theme.continuationPromptColor
            ConsoleSegmentRole.COMMAND -> theme.commandColor
            ConsoleSegmentRole.OUTPUT -> theme.outputColor
            ConsoleSegmentRole.STATUS -> theme.statusColor
            ConsoleSegmentRole.ERROR -> theme.errorColor
        }
        withStyle(style = SpanStyle(color = color)) { append(segment.text) }
    }
}
