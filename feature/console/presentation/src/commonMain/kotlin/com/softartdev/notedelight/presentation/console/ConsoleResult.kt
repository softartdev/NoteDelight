package com.softartdev.notedelight.presentation.console

import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntry

data class ConsoleResult(
    val input: String = "",
    val running: Boolean = false,
    val transcript: List<ConsoleTranscriptEntry> = emptyList(),
    val commandHistory: List<String> = emptyList(),
)
