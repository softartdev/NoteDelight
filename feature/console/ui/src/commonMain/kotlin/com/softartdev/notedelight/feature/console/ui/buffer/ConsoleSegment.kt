package com.softartdev.notedelight.feature.console.ui.buffer

/**
 * A run of text on a single buffer line with a uniform visual role. A line is composed of
 * one or more segments: e.g. `[PROMPT "sqlite> ", COMMAND "SELECT 1;"]`.
 */
data class ConsoleSegment(
    val text: String,
    val role: ConsoleSegmentRole,
)
