package com.softartdev.notedelight.presentation.console

import app.cash.turbine.test
import com.softartdev.notedelight.db.DatabaseHolder
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntry
import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntryKind
import com.softartdev.notedelight.usecase.console.ConsoleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ConsoleViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepo: FakeSafeRepo
    private lateinit var viewModel: ConsoleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeSafeRepo()
        viewModel = ConsoleViewModel(ConsoleUseCase(fakeRepo))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeSafeRepo(
        var result: String? = null,
        var exception: Exception? = null,
    ) : SafeRepo() {
        override val databaseState: PlatformSQLiteState = PlatformSQLiteState.UNENCRYPTED
        override val noteDAO: NoteDAO get() = error("Not needed in test")
        override val dbPath: String = "/fake/path"

        override suspend fun buildDbIfNeed(passphrase: CharSequence): DatabaseHolder = error("Not needed in test")
        override suspend fun decrypt(oldPass: CharSequence) = Unit
        override suspend fun rekey(oldPass: CharSequence, newPass: CharSequence) = Unit
        override suspend fun encrypt(newPass: CharSequence) = Unit
        override suspend fun execute(query: String): String? {
            exception?.let { throw it }
            return result
        }
        override suspend fun closeDatabase() = Unit
    }

    @Test
    fun initialStateIsEmpty() = runTest {
        viewModel.stateFlow.test {
            val state = awaitItem()
            assertEquals("", state.input)
            assertFalse(state.running)
            assertTrue(state.transcript.isEmpty())
            assertTrue(state.commandHistory.isEmpty())
        }
    }

    @Test
    fun autofillSetsInputWithoutExecuting() = runTest {
        viewModel.stateFlow.test {
            awaitItem() // initial
            viewModel.onAction(ConsoleAction.UpdateInput("SELECT 1;"))
            val state = awaitItem()
            assertEquals("SELECT 1;", state.input)
            assertTrue(state.transcript.isEmpty())
        }
    }

    @Test
    fun updateInputChangesText() = runTest {
        viewModel.stateFlow.test {
            awaitItem() // initial
            viewModel.onAction(ConsoleAction.UpdateInput("PRAGMA"))
            assertEquals("PRAGMA", awaitItem().input)
        }
    }

    @Test
    fun submitWithValidInputAppendsTranscript() = runTest {
        fakeRepo.result = "0"
        viewModel.onAction(ConsoleAction.UpdateInput("SELECT last_insert_rowid()"))
        viewModel.onAction(ConsoleAction.Submit)

        val state = viewModel.stateFlow.value
        assertFalse(state.running)
        assertEquals("", state.input)
        assertEquals(3, state.transcript.size)
        assertEquals(ConsoleTranscriptEntryKind.COMMAND, state.transcript[0].kind)
        assertEquals("SELECT last_insert_rowid();", state.transcript[0].text)
        assertEquals(ConsoleTranscriptEntryKind.OUTPUT, state.transcript[1].kind)
        assertEquals("0", state.transcript[1].text)
        assertEquals(ConsoleTranscriptEntryKind.STATUS, state.transcript[2].kind)
    }

    @Test
    fun submitClearsInputAndAddsToHistory() = runTest {
        fakeRepo.result = null
        viewModel.onAction(ConsoleAction.UpdateInput("CREATE TABLE t (id INTEGER);"))
        viewModel.onAction(ConsoleAction.Submit)

        val state = viewModel.stateFlow.value
        assertEquals("", state.input)
        assertEquals(1, state.commandHistory.size)
        assertEquals("CREATE TABLE t (id INTEGER);", state.commandHistory[0])
    }

    @Test
    fun submitWithBlankInputDoesNothing() = runTest {
        viewModel.onAction(ConsoleAction.UpdateInput("   "))
        viewModel.onAction(ConsoleAction.Submit)

        val state = viewModel.stateFlow.value
        assertTrue(state.transcript.isEmpty())
        assertTrue(state.commandHistory.isEmpty())
        assertFalse(state.running)
    }

    @Test
    fun executionErrorAppendsCommandThenError() = runTest {
        fakeRepo.exception = RuntimeException("no such table")
        viewModel.onAction(ConsoleAction.UpdateInput("SELECT * FROM missing"))
        viewModel.onAction(ConsoleAction.Submit)

        val state = viewModel.stateFlow.value
        assertEquals(2, state.transcript.size)
        assertEquals(ConsoleTranscriptEntryKind.COMMAND, state.transcript[0].kind)
        assertEquals("SELECT * FROM missing;", state.transcript[0].text)
        assertEquals(ConsoleTranscriptEntryKind.ERROR, state.transcript[1].kind)
        assertEquals("no such table", state.transcript[1].text)
        assertEquals("", state.input)
        assertEquals(listOf("SELECT * FROM missing;"), state.commandHistory)
    }

    @Test
    fun multipleExecutesAccumulateTranscript() = runTest {
        fakeRepo.result = "1"
        viewModel.onAction(ConsoleAction.UpdateInput("SELECT 1;"))
        viewModel.onAction(ConsoleAction.Submit)

        fakeRepo.result = "2"
        viewModel.onAction(ConsoleAction.UpdateInput("SELECT 2;"))
        viewModel.onAction(ConsoleAction.Submit)

        val state = viewModel.stateFlow.value
        assertEquals(6, state.transcript.size) // 2 commands * (COMMAND + OUTPUT + STATUS)
        assertEquals(2, state.commandHistory.size)
    }

    @Test
    fun historyIsSessionOnly() = runTest {
        // History starts empty and is not persisted
        assertTrue(viewModel.stateFlow.value.commandHistory.isEmpty())

        fakeRepo.result = null
        viewModel.onAction(ConsoleAction.UpdateInput("SELECT 1;"))
        viewModel.onAction(ConsoleAction.Submit)

        assertEquals(1, viewModel.stateFlow.value.commandHistory.size)

        // Creating a new ViewModel simulates app restart
        val newVm = ConsoleViewModel(ConsoleUseCase(fakeRepo))
        assertTrue(newVm.stateFlow.value.commandHistory.isEmpty())
    }

}
