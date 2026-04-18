package com.softartdev.notedelight.usecase.console

enum class ConsoleTranscriptEntryKind {
    COMMAND,
    OUTPUT,
    STATUS,
    ERROR,
}

data class ConsoleTranscriptEntry(
    val kind: ConsoleTranscriptEntryKind,
    val text: String,
)
