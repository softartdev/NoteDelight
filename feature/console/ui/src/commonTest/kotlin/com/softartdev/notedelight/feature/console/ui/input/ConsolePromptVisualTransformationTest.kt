package com.softartdev.notedelight.feature.console.ui.input

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import kotlin.test.Test
import kotlin.test.assertEquals

class ConsolePromptVisualTransformationTest {

    private val prompt: String = "    ...> "
    private val transformation = ConsolePromptVisualTransformation(
        continuationPrompt = prompt,
        continuationPromptStyle = SpanStyle(color = Color.Red),
    )

    private fun transform(input: String): TransformedText =
        transformation.filter(text = AnnotatedString(text = input))

    @Test
    fun emptyInputIsPassthrough() {
        val transformed: TransformedText = transform(input = "")
        assertEquals(expected = "", actual = transformed.text.text)
        assertEquals(
            expected = OffsetMapping.Identity,
            actual = transformed.offsetMapping,
        )
    }

    @Test
    fun inputWithoutNewlineIsPassthrough() {
        val input = "SELECT *"
        val transformed: TransformedText = transform(input = input)
        assertEquals(expected = input, actual = transformed.text.text)
        // Identity mapping when no newlines present.
        assertEquals(
            expected = 0,
            actual = transformed.offsetMapping.originalToTransformed(offset = 0),
        )
        assertEquals(
            expected = input.length,
            actual = transformed.offsetMapping.originalToTransformed(offset = input.length),
        )
    }

    @Test
    fun singleNewlineInsertsContinuationPromptAfterIt() {
        val input = "SELECT *\nFROM note"
        val transformed: TransformedText = transform(input = input)
        assertEquals(expected = "SELECT *\n${prompt}FROM note", actual = transformed.text.text)
    }

    @Test
    fun multipleNewlinesInsertOnePromptEach() {
        val input = "a\nb\nc"
        val transformed: TransformedText = transform(input = input)
        val expected = "a\n${prompt}b\n${prompt}c"
        assertEquals(expected = expected, actual = transformed.text.text)
    }

    @Test
    fun originalToTransformedAccountsForPromptShifts() {
        val input = "ab\ncd\nef"
        val transformed: TransformedText = transform(input = input)
        val mapping: OffsetMapping = transformed.offsetMapping
        // Original positions: a=0 b=1 \n=2 c=3 d=4 \n=5 e=6 f=7 (length=8)
        // Transformed: "ab\n{prompt}cd\n{prompt}ef" — promptLength = 9
        assertEquals(expected = 0, actual = mapping.originalToTransformed(offset = 0))
        assertEquals(expected = 1, actual = mapping.originalToTransformed(offset = 1))
        // Newline itself stays at the same transformed position.
        assertEquals(expected = 2, actual = mapping.originalToTransformed(offset = 2))
        // 'c' (original 3) sits *after* the newline + 1 prompt = 3 + 9 = 12
        assertEquals(expected = 12, actual = mapping.originalToTransformed(offset = 3))
        // 'd' (original 4) → 4 + 9 = 13
        assertEquals(expected = 13, actual = mapping.originalToTransformed(offset = 4))
        // Second newline (original 5) → 5 + 9 = 14
        assertEquals(expected = 14, actual = mapping.originalToTransformed(offset = 5))
        // 'e' (original 6) → 6 + 18 = 24
        assertEquals(expected = 24, actual = mapping.originalToTransformed(offset = 6))
        // End of input (original 8) → 8 + 18 = 26
        assertEquals(expected = 26, actual = mapping.originalToTransformed(offset = 8))
    }

    @Test
    fun transformedToOriginalRoundTrips() {
        val input = "ab\ncd\nef"
        val transformed: TransformedText = transform(input = input)
        val mapping: OffsetMapping = transformed.offsetMapping
        for (i in 0..input.length) {
            val tx = mapping.originalToTransformed(offset = i)
            val back = mapping.transformedToOriginal(offset = tx)
            assertEquals(expected = i, actual = back, message = "round trip failed at $i")
        }
    }

    @Test
    fun transformedToOriginalSnapsCaretInsidePromptToAfterNewline() {
        val input = "a\nb"
        val transformed: TransformedText = transform(input = input)
        val mapping: OffsetMapping = transformed.offsetMapping
        // Transformed: "a\n{prompt}b" — positions: a=0 \n=1 prompt[2..10] b=11
        // A caret landing anywhere inside the synthetic prompt region snaps to "just after \n"
        // in the original (offset 2).
        for (offset in 3..10) {
            assertEquals(
                expected = 2,
                actual = mapping.transformedToOriginal(offset = offset),
                message = "expected snap-to-after-newline at transformed offset $offset",
            )
        }
        // The position right at b (offset 11) is original offset 2 (start of "b").
        assertEquals(expected = 2, actual = mapping.transformedToOriginal(offset = 11))
    }

    @Test
    fun trailingNewlineProducesTrailingPrompt() {
        val input = "SELECT 1;\n"
        val transformed: TransformedText = transform(input = input)
        assertEquals(expected = "SELECT 1;\n${prompt}", actual = transformed.text.text)
        // Original end-of-string maps to the very end of the transformed text (after prompt).
        val end = transformed.offsetMapping.originalToTransformed(offset = input.length)
        assertEquals(expected = transformed.text.text.length, actual = end)
    }

    @Test
    fun outOfRangeOffsetsAreClampedSafely() {
        val input = "a\nb"
        val transformed: TransformedText = transform(input = input)
        val mapping: OffsetMapping = transformed.offsetMapping
        // Negative original offsets clamp to start.
        assertEquals(expected = 0, actual = mapping.originalToTransformed(offset = -5))
        // Past-end original offsets clamp to end.
        val end = transformed.text.text.length
        assertEquals(expected = end, actual = mapping.originalToTransformed(offset = 999))
        // Negative transformed offsets clamp to original start.
        assertEquals(expected = 0, actual = mapping.transformedToOriginal(offset = -3))
        // Past-end transformed offsets clamp to original end.
        assertEquals(expected = input.length, actual = mapping.transformedToOriginal(offset = 999))
    }
}
