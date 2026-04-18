package com.softartdev.notedelight.feature.console.ui.buffer

import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntry
import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntryKind

/**
 * Pure mapping from presentation-layer transcript entries to a renderable buffer.
 *
 * - A [ConsoleTranscriptEntryKind.COMMAND] entry becomes one buffer line per `\n`-separated
 *   sub-line. The first sub-line is prefixed with [PROMPT_TEXT] as a [ConsoleSegmentRole.PROMPT]
 *   segment; subsequent sub-lines get [CONTINUATION_PROMPT_TEXT] as
 *   [ConsoleSegmentRole.CONTINUATION_PROMPT].
 * - [OUTPUT] / [STATUS] / [ERROR] entries are split on `\n` and emitted as plain lines with
 *   the matching role; no prompt prefix.
 */
object ConsoleBufferBuilder {

    const val PROMPT_TEXT: String = "sqlite> "
    const val CONTINUATION_PROMPT_TEXT: String = "    ...> "

    fun build(transcript: List<ConsoleTranscriptEntry>): ConsoleBuffer {
        if (transcript.isEmpty()) return ConsoleBuffer.EMPTY
        val lines: MutableList<ConsoleBufferLine> = mutableListOf()
        for (entry in transcript) {
            when (entry.kind) {
                ConsoleTranscriptEntryKind.COMMAND -> appendCommand(lines, entry.text)
                ConsoleTranscriptEntryKind.OUTPUT -> appendPlain(lines, entry.text, ConsoleSegmentRole.OUTPUT)
                ConsoleTranscriptEntryKind.STATUS -> appendPlain(lines, entry.text, ConsoleSegmentRole.STATUS)
                ConsoleTranscriptEntryKind.ERROR -> appendPlain(lines, entry.text, ConsoleSegmentRole.ERROR)
            }
        }
        return ConsoleBuffer(lines = lines.toList())
    }

    private fun appendCommand(lines: MutableList<ConsoleBufferLine>, raw: String) {
        val subLines: List<String> = raw.split('\n')
        subLines.forEachIndexed { index, subLine ->
            val prompt: ConsoleSegment = if (index == 0) {
                ConsoleSegment(text = PROMPT_TEXT, role = ConsoleSegmentRole.PROMPT)
            } else {
                ConsoleSegment(text = CONTINUATION_PROMPT_TEXT, role = ConsoleSegmentRole.CONTINUATION_PROMPT)
            }
            val command = ConsoleSegment(text = subLine, role = ConsoleSegmentRole.COMMAND)
            lines += ConsoleBufferLine(segments = listOf(prompt, command))
        }
    }

    private fun appendPlain(
        lines: MutableList<ConsoleBufferLine>,
        raw: String,
        role: ConsoleSegmentRole,
    ) {
        for (subLine in raw.split('\n')) {
            lines += ConsoleBufferLine(segments = listOf(ConsoleSegment(text = subLine, role = role)))
        }
    }
}
