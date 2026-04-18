package com.softartdev.notedelight.feature.console.ui.input

/**
 * Pure, UI-side heuristic that decides whether the current input text should be treated as
 * a complete statement and executed, or left open for continuation on the next line.
 *
 * The domain-layer [com.softartdev.notedelight.usecase.console.ConsoleUseCase] does not care
 * about completeness — it trims and auto-appends `;` — so this is purely a UX decision:
 * pressing Enter on a complete statement submits it; on an incomplete statement it inserts
 * a newline and the input continues with a `...>` continuation prompt.
 *
 * Rules (deliberately simple, not a SQL parser):
 *   1. Trim whitespace.
 *   2. Empty → incomplete.
 *   3. Starts with `.` → complete (dot-command, always a single-line shell command).
 *   4. Ends with `;` → complete.
 *   5. Otherwise → incomplete.
 *
 * Known edge cases: unbalanced `;` inside string literals or line comments are not detected.
 * These are acceptable trade-offs for v1 — see module README for the roadmap.
 */
object ConsoleStatementAnalyzer {

    fun isComplete(raw: String): Boolean {
        val trimmed: String = raw.trim()
        if (trimmed.isEmpty()) return false
        if (trimmed.startsWith(".")) return true
        return trimmed.endsWith(";")
    }
}
