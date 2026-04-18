package com.softartdev.notedelight.feature.console.ui.buffer

/**
 * Read-only scrollback model. The active (editable) input line is deliberately *not* part of
 * the buffer — it lives in a sibling [androidx.compose.foundation.text.BasicTextField] — so the
 * buffer represents only committed history.
 */
data class ConsoleBuffer(val lines: List<ConsoleBufferLine>) {

    val lineCount: Int get() = lines.size

    val isEmpty: Boolean get() = lines.isEmpty()

    /**
     * Plain text representation of the whole buffer, joined with `\n`. Used as the
     * `contentDescription` for the Canvas-drawn history so screen readers and text-based test
     * queries can still see the content (Canvas text has no default semantics).
     */
    fun plainText(): String = lines.joinToString(separator = "\n") { it.rawText }

    companion object {
        val EMPTY: ConsoleBuffer = ConsoleBuffer(lines = emptyList())
    }
}
