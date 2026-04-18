package com.softartdev.notedelight.usecase.console

import com.softartdev.notedelight.db.DatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ConsoleUseCaseTest {

    private class FakeSafeRepo(
        var result: String? = null,
        var exception: Exception? = null,
    ) : SafeRepo() {
        var lastSql: String? = null
        override val databaseState: PlatformSQLiteState = PlatformSQLiteState.UNENCRYPTED
        override val noteDAO: NoteDAO get() = error("Not needed in test")
        override val dbPath: String = "/fake/path"

        override suspend fun buildDbIfNeed(passphrase: CharSequence): DatabaseHolder = error("Not needed in test")
        override suspend fun decrypt(oldPass: CharSequence) = Unit
        override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) = Unit
        override suspend fun encrypt(newPass: CharSequence) = Unit
        override suspend fun execute(query: String): String? {
            lastSql = query
            exception?.let { throw it }
            return result
        }
        override suspend fun closeDatabase() = Unit
    }

    @Test
    fun blankInputReturnsValidationError() = runTest {
        val repo = FakeSafeRepo()
        val useCase = ConsoleUseCase(repo)

        val result = useCase("   ")
        assertIs<ConsoleUseCaseResult.ValidationError>(result)
        assertEquals("Enter a SQL statement.", result.message)
    }

    @Test
    fun emptyInputReturnsValidationError() = runTest {
        val repo = FakeSafeRepo()
        val useCase = ConsoleUseCase(repo)

        val result = useCase("")
        assertIs<ConsoleUseCaseResult.ValidationError>(result)
        assertEquals("Enter a SQL statement.", result.message)
    }

    @Test
    fun semicolonAppendedWhenMissing() = runTest {
        val repo = FakeSafeRepo(result = "42")
        val useCase = ConsoleUseCase(repo)

        val result = useCase("SELECT 1")
        assertIs<ConsoleUseCaseResult.Executed>(result)
        assertEquals("SELECT 1;", repo.lastSql)
        assertEquals("SELECT 1;", result.normalizedCommand)
        assertEquals(2, result.entries.size)
        assertEquals(ConsoleTranscriptEntryKind.OUTPUT, result.entries[0].kind)
        assertEquals("42", result.entries[0].text)
        assertEquals(ConsoleTranscriptEntryKind.STATUS, result.entries[1].kind)
        assertEquals("Query returned 1 row(s).", result.entries[1].text)
    }

    @Test
    fun semicolonPreservedWhenPresent() = runTest {
        val repo = FakeSafeRepo(result = "42")
        val useCase = ConsoleUseCase(repo)

        val result = useCase("SELECT 1;")
        assertIs<ConsoleUseCaseResult.Executed>(result)
        assertEquals("SELECT 1;", repo.lastSql)
        assertEquals("SELECT 1;", result.normalizedCommand)
    }

    @Test
    fun queryReturningValueReturnsOutputAndStatus() = runTest {
        val repo = FakeSafeRepo(result = "hello")
        val useCase = ConsoleUseCase(repo)

        val result = useCase("SELECT 'hello';")
        assertIs<ConsoleUseCaseResult.Executed>(result)
        assertEquals("SELECT 'hello';", result.normalizedCommand)
        assertEquals(2, result.entries.size)
        assertEquals(ConsoleTranscriptEntryKind.OUTPUT, result.entries[0].kind)
        assertEquals("hello", result.entries[0].text)
        assertEquals(ConsoleTranscriptEntryKind.STATUS, result.entries[1].kind)
        assertEquals("Query returned 1 row(s).", result.entries[1].text)
    }

    @Test
    fun statementReturningNullReturnsOnlyStatus() = runTest {
        val repo = FakeSafeRepo(result = null)
        val useCase = ConsoleUseCase(repo)

        val result = useCase("INSERT INTO note VALUES (1, 'test');")
        assertIs<ConsoleUseCaseResult.Executed>(result)
        assertEquals("INSERT INTO note VALUES (1, 'test');", result.normalizedCommand)
        assertEquals(1, result.entries.size)
        assertEquals(ConsoleTranscriptEntryKind.STATUS, result.entries[0].kind)
        assertEquals("Statement executed successfully.", result.entries[0].text)
    }

    @Test
    fun exceptionReturnsExecutedWithErrorEntry() = runTest {
        val repo = FakeSafeRepo(exception = RuntimeException("table not found"))
        val useCase = ConsoleUseCase(repo)

        val result = useCase("SELECT * FROM missing;")
        assertIs<ConsoleUseCaseResult.Executed>(result)
        assertEquals("SELECT * FROM missing;", result.normalizedCommand)
        assertEquals(1, result.entries.size)
        assertEquals(ConsoleTranscriptEntryKind.ERROR, result.entries[0].kind)
        assertEquals("table not found", result.entries[0].text)
    }

    @Test
    fun exceptionWithNullMessageUsesDefault() = runTest {
        val repo = FakeSafeRepo(exception = RuntimeException())
        val useCase = ConsoleUseCase(repo)

        val result = useCase("BAD SQL")
        assertIs<ConsoleUseCaseResult.Executed>(result)
        assertEquals("BAD SQL;", result.normalizedCommand)
        assertEquals("Console command failed.", result.entries[0].text)
    }

    @Test
    fun inputIsTrimmed() = runTest {
        val repo = FakeSafeRepo(result = null)
        val useCase = ConsoleUseCase(repo)

        val result = useCase("  SELECT 1;  ")
        assertIs<ConsoleUseCaseResult.Executed>(result)
        assertEquals("SELECT 1;", repo.lastSql)
    }
}
