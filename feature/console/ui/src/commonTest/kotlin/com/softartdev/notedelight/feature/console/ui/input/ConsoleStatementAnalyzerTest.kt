package com.softartdev.notedelight.feature.console.ui.input

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsoleStatementAnalyzerTest {

    @Test
    fun blankIsIncomplete() {
        assertFalse(ConsoleStatementAnalyzer.isComplete(raw = ""))
        assertFalse(ConsoleStatementAnalyzer.isComplete(raw = "   "))
        assertFalse(ConsoleStatementAnalyzer.isComplete(raw = "\n\n"))
        assertFalse(ConsoleStatementAnalyzer.isComplete(raw = " \t \n"))
    }

    @Test
    fun semicolonTailIsComplete() {
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = "SELECT 1;"))
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = "SELECT * FROM note;"))
    }

    @Test
    fun semicolonTailIsCompleteWithSurroundingWhitespace() {
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = "  SELECT 1;  "))
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = "SELECT 1;\n"))
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = "\n SELECT 1; \n"))
    }

    @Test
    fun dotPrefixIsCompleteEvenWithoutSemicolon() {
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = ".help"))
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = ".tables"))
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = "  .schema  "))
    }

    @Test
    fun multilineWithSemicolonOnLastLineIsComplete() {
        val sql = "SELECT *\nFROM note\nWHERE id = 1;"
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = sql))
    }

    @Test
    fun multilineWithoutTrailingSemicolonIsIncomplete() {
        val sql = "SELECT *\nFROM note"
        assertFalse(ConsoleStatementAnalyzer.isComplete(raw = sql))
    }

    @Test
    fun statementWithoutSemicolonOrDotIsIncomplete() {
        assertFalse(ConsoleStatementAnalyzer.isComplete(raw = "SELECT 1"))
        assertFalse(ConsoleStatementAnalyzer.isComplete(raw = "PRAGMA cipher_version"))
    }

    @Test
    fun semicolonInTheMiddleAlsoCountsAsCompleteWhenStatementEndsWithSemicolon() {
        // Multi-statement input; both end in `;` so it's "complete enough" to dispatch.
        assertTrue(ConsoleStatementAnalyzer.isComplete(raw = "SELECT 1; SELECT 2;"))
    }
}
