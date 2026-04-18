package com.softartdev.notedelight.feature.console.ui.buffer

/**
 * One visual line of the terminal scrollback. The concatenation of [segments].text is the raw
 * line content; segments only differ in their semantic [ConsoleSegmentRole] for rendering.
 */
data class ConsoleBufferLine(val segments: List<ConsoleSegment>) {

    /** Raw textual content of this line (no role annotations). */
    val rawText: String = segments.joinToString(separator = "") { it.text }
}
