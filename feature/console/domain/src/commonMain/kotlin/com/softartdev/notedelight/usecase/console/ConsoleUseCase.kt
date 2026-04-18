package com.softartdev.notedelight.usecase.console

import com.softartdev.notedelight.repository.SafeRepo

class ConsoleUseCase(private val safeRepo: SafeRepo) {

    suspend operator fun invoke(rawInput: String): ConsoleUseCaseResult {
        val trimmed: String = rawInput.trim()
        if (trimmed.isBlank()) return ConsoleUseCaseResult.ValidationError("Enter a SQL statement.")
        val sql: String = if (trimmed.endsWith(";")) trimmed else "$trimmed;"
        return try {
            val entries = when (val result: String? = safeRepo.execute(sql)) {
                null -> listOf(
                    ConsoleTranscriptEntry(
                        kind = ConsoleTranscriptEntryKind.STATUS,
                        text = "Statement executed successfully."
                    ),
                )
                else -> listOf(
                    ConsoleTranscriptEntry(
                        kind = ConsoleTranscriptEntryKind.OUTPUT,
                        text = result
                    ),
                    ConsoleTranscriptEntry(
                        kind = ConsoleTranscriptEntryKind.STATUS,
                        text = "Query returned 1 row(s)."
                    ),
                )
            }
            ConsoleUseCaseResult.Executed(entries = entries, normalizedCommand = sql)
        } catch (t: Throwable) {
            val msg = t.message ?: "Console command failed."
            ConsoleUseCaseResult.Executed(
                entries = listOf(ConsoleTranscriptEntry(ConsoleTranscriptEntryKind.ERROR, msg)),
                normalizedCommand = sql,
            )
        }
    }
}
