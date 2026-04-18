package com.softartdev.notedelight.feature.console.ui.buffer

/**
 * Semantic role of a [ConsoleSegment] inside a [ConsoleBufferLine]. Drives color selection
 * via `ConsoleTheme` and — in the future — syntax-aware highlighting or accessibility labels.
 */
enum class ConsoleSegmentRole {
    /** Leading "sqlite> " prompt rendered at the start of a fresh command line. */
    PROMPT,

    /** Leading "    ...> " prompt rendered on continuation lines of a multi-line command. */
    CONTINUATION_PROMPT,

    /** User-entered SQL echoed back into the history. */
    COMMAND,

    /** Query result output. */
    OUTPUT,

    /** Informational status message (e.g. "Statement executed successfully."). */
    STATUS,

    /** Error message produced by a failed execution. */
    ERROR,
}
