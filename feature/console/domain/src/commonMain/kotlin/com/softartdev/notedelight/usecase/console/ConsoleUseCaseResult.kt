package com.softartdev.notedelight.usecase.console

sealed class ConsoleUseCaseResult {

    data class Executed(
        val entries: List<ConsoleTranscriptEntry>,
        val normalizedCommand: String,
    ) : ConsoleUseCaseResult()

    data class ValidationError(val message: String) : ConsoleUseCaseResult()
}
