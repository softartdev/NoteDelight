package com.softartdev.notedelight.feature.console.ui.buffer

import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntry
import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntryKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ConsoleBufferBuilderTest {

    @Test
    fun emptyTranscriptYieldsEmptyBuffer() {
        val buffer: ConsoleBuffer = ConsoleBufferBuilder.build(transcript = emptyList())
        assertSame(expected = ConsoleBuffer.EMPTY, actual = buffer)
        assertTrue(buffer.isEmpty)
        assertEquals(expected = 0, actual = buffer.lineCount)
    }

    @Test
    fun singleLineCommandHasPromptPrefix() {
        val transcript = listOf(
            ConsoleTranscriptEntry(kind = ConsoleTranscriptEntryKind.COMMAND, text = "SELECT 1;"),
        )
        val buffer: ConsoleBuffer = ConsoleBufferBuilder.build(transcript = transcript)
        assertEquals(expected = 1, actual = buffer.lineCount)
        val line = buffer.lines.single()
        assertEquals(expected = 2, actual = line.segments.size)
        assertEquals(
            expected = ConsoleSegment(
                text = ConsoleBufferBuilder.PROMPT_TEXT,
                role = ConsoleSegmentRole.PROMPT,
            ),
            actual = line.segments[0],
        )
        assertEquals(
            expected = ConsoleSegment(text = "SELECT 1;", role = ConsoleSegmentRole.COMMAND),
            actual = line.segments[1],
        )
    }

    @Test
    fun multilineCommandPrefixesContinuationOnFollowingLines() {
        val sql = "SELECT *\nFROM note\nWHERE id = 1;"
        val transcript = listOf(
            ConsoleTranscriptEntry(kind = ConsoleTranscriptEntryKind.COMMAND, text = sql),
        )
        val buffer: ConsoleBuffer = ConsoleBufferBuilder.build(transcript = transcript)
        assertEquals(expected = 3, actual = buffer.lineCount)

        val firstPrompt = buffer.lines[0].segments[0]
        assertEquals(expected = ConsoleSegmentRole.PROMPT, actual = firstPrompt.role)
        assertEquals(expected = ConsoleBufferBuilder.PROMPT_TEXT, actual = firstPrompt.text)

        val secondPrompt = buffer.lines[1].segments[0]
        assertEquals(
            expected = ConsoleSegmentRole.CONTINUATION_PROMPT,
            actual = secondPrompt.role,
        )
        assertEquals(
            expected = ConsoleBufferBuilder.CONTINUATION_PROMPT_TEXT,
            actual = secondPrompt.text,
        )
        assertEquals(expected = "FROM note", actual = buffer.lines[1].segments[1].text)

        val thirdPrompt = buffer.lines[2].segments[0]
        assertEquals(
            expected = ConsoleSegmentRole.CONTINUATION_PROMPT,
            actual = thirdPrompt.role,
        )
        assertEquals(expected = "WHERE id = 1;", actual = buffer.lines[2].segments[1].text)
    }

    @Test
    fun outputStatusErrorEntriesProduceMatchingRolesWithoutPrompt() {
        val transcript = listOf(
            ConsoleTranscriptEntry(kind = ConsoleTranscriptEntryKind.OUTPUT, text = "1|hello"),
            ConsoleTranscriptEntry(
                kind = ConsoleTranscriptEntryKind.STATUS,
                text = "Query returned 1 row(s).",
            ),
            ConsoleTranscriptEntry(
                kind = ConsoleTranscriptEntryKind.ERROR,
                text = "syntax error near 'foo'",
            ),
        )
        val buffer: ConsoleBuffer = ConsoleBufferBuilder.build(transcript = transcript)
        assertEquals(expected = 3, actual = buffer.lineCount)
        assertEquals(
            expected = listOf(ConsoleSegmentRole.OUTPUT),
            actual = buffer.lines[0].segments.map { it.role },
        )
        assertEquals(
            expected = listOf(ConsoleSegmentRole.STATUS),
            actual = buffer.lines[1].segments.map { it.role },
        )
        assertEquals(
            expected = listOf(ConsoleSegmentRole.ERROR),
            actual = buffer.lines[2].segments.map { it.role },
        )
    }

    @Test
    fun multilineOutputSplitsOnNewline() {
        val transcript = listOf(
            ConsoleTranscriptEntry(
                kind = ConsoleTranscriptEntryKind.OUTPUT,
                text = "row1\nrow2\nrow3",
            ),
        )
        val buffer: ConsoleBuffer = ConsoleBufferBuilder.build(transcript = transcript)
        assertEquals(expected = 3, actual = buffer.lineCount)
        assertEquals(expected = "row1", actual = buffer.lines[0].rawText)
        assertEquals(expected = "row2", actual = buffer.lines[1].rawText)
        assertEquals(expected = "row3", actual = buffer.lines[2].rawText)
        for (line in buffer.lines) {
            assertEquals(expected = ConsoleSegmentRole.OUTPUT, actual = line.segments.single().role)
        }
    }

    @Test
    fun plainTextJoinsAllLinesWithNewlines() {
        val transcript = listOf(
            ConsoleTranscriptEntry(kind = ConsoleTranscriptEntryKind.COMMAND, text = "SELECT 1;"),
            ConsoleTranscriptEntry(kind = ConsoleTranscriptEntryKind.OUTPUT, text = "1"),
            ConsoleTranscriptEntry(
                kind = ConsoleTranscriptEntryKind.STATUS,
                text = "Query returned 1 row(s).",
            ),
        )
        val buffer: ConsoleBuffer = ConsoleBufferBuilder.build(transcript = transcript)
        val expected = "${ConsoleBufferBuilder.PROMPT_TEXT}SELECT 1;\n1\nQuery returned 1 row(s)."
        assertEquals(expected = expected, actual = buffer.plainText())
    }
}
