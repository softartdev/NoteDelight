package com.softartdev.notedelight.feature.console.ui.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

/**
 * Visual-only transformation that prepends a continuation prompt (e.g. `"    ...> "`) after
 * every `\n` in the input. The leading `"sqlite> "` prefix for the first line is *not*
 * inserted by this transformation — it is rendered as a sibling `Text` in the input row — so
 * that the caret naturally lands at column 0 of the real input when the field is empty.
 *
 * [OffsetMapping] precisely translates between logical input offsets (what
 * `BasicTextField` persists) and visual offsets (what the user sees). A wrong mapping makes
 * the caret land mid-word when moving left/right or tapping, so the mapping is exhaustively
 * unit-tested in `ConsolePromptVisualTransformationTest`.
 *
 * Note: this works on the string representation. Long lines that wrap visually *without* an
 * explicit `\n` do not trigger continuation — a known, accepted limitation.
 */
class ConsolePromptVisualTransformation(
    private val continuationPrompt: String,
    private val continuationPromptStyle: SpanStyle,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val original: String = text.text
        if ('\n' !in original) {
            return TransformedText(text = text, offsetMapping = OffsetMapping.Identity)
        }

        val transformed: AnnotatedString = buildAnnotatedString {
            val lines: List<String> = original.split('\n')
            lines.forEachIndexed { index, line ->
                if (index != 0) {
                    append('\n')
                    withStyle(style = continuationPromptStyle) { append(continuationPrompt) }
                }
                append(line)
            }
        }

        val mapping: OffsetMapping = ContinuationOffsetMapping(
            original = original,
            promptLength = continuationPrompt.length,
            transformedLength = transformed.length,
        )
        return TransformedText(text = transformed, offsetMapping = mapping)
    }

    private class ContinuationOffsetMapping(
        private val original: String,
        private val promptLength: Int,
        private val transformedLength: Int,
    ) : OffsetMapping {

        // newlinesBefore[i] = number of '\n' characters in original[0 until i].
        private val newlinesBefore: IntArray = IntArray(size = original.length + 1).also { arr ->
            var running = 0
            for (i in original.indices) {
                arr[i] = running
                if (original[i] == '\n') running++
            }
            arr[original.length] = running
        }

        override fun originalToTransformed(offset: Int): Int {
            val clamped: Int = offset.coerceIn(minimumValue = 0, maximumValue = original.length)
            return clamped + newlinesBefore[clamped] * promptLength
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 0) return 0
            if (offset >= transformedLength) return original.length
            var transformedPos = 0
            for (i in 0..original.length) {
                if (transformedPos >= offset) return i
                if (i < original.length) {
                    val ch = original[i]
                    transformedPos += 1
                    if (ch == '\n') {
                        // Chars [transformedPos, transformedPos + promptLength) are the
                        // synthetic prompt; any caret landing inside (or exactly at the start
                        // of) that region snaps to "just after the newline" in original.
                        if (transformedPos + promptLength >= offset) return i + 1
                        transformedPos += promptLength
                    }
                }
            }
            return original.length
        }
    }
}
