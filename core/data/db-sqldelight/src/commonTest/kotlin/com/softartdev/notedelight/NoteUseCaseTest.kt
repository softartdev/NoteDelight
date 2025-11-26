package com.softartdev.notedelight

import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.db.dbo
import com.softartdev.notedelight.db.toModel
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import com.softartdev.notedelight.model.Note as NoteModel

@ExperimentalCoroutinesApi
class NoteUseCaseTest : BaseTest() {

    private var noteDAO: NoteDAO? = null
    private var createNoteUseCase: CreateNoteUseCase? = null
    private var saveNoteUseCase: SaveNoteUseCase? = null
    private var updateTitleUseCase: UpdateTitleUseCase? = null

    private val notes: List<Note> = TestSchema.notes.map(NoteModel::dbo)

    private val mutex = Mutex()

    @BeforeTest
    fun setUp() = runTest {
        Logger.setLogWriters(PrintLogWriter())
        val noteDB = noteDB()
        noteDAO = safeRepo.noteDAO
        createNoteUseCase = CreateNoteUseCase(noteDAO!!)
        saveNoteUseCase = SaveNoteUseCase(noteDAO!!)
        updateTitleUseCase = UpdateTitleUseCase(noteDAO!!)
        TestSchema.insertTestNotes(noteDB.noteQueries)
    }

    @AfterTest
    fun tearDown() = runTest {
        noteDAO!!.deleteAll()
        noteDAO = null
        createNoteUseCase = null
        saveNoteUseCase = null
        updateTitleUseCase = null
        Logger.setLogWriters()
    }

    @Test
    fun getTitleChannel() = runTest {
        val act = "test title"
        val deferred = async { UpdateTitleUseCase.dialogChannel.receive() }
        UpdateTitleUseCase.dialogChannel.send(act)
        val exp = deferred.await()
        assertEquals(exp, act)
    }

    @Test
    fun getNotes() = runTest {
        assertContentEquals(notes.toModel(), noteDAO!!.listFlow.first())
    }

    @Test
    fun createNote() = runTest {
        val lastId = notes.maxByOrNull(Note::id)?.id ?: 0
        val newId = lastId + 1
        assertEquals(newId, createNoteUseCase!!.invoke())
    }

    @Test
    fun saveNote() = runTest {
        val id: Long = 2
        val newTitle = "new title"
        val newText = "new text"
        saveNoteUseCase!!.invoke(id, newTitle, newText)
        val updatedNote = noteDAO!!.load(id)
        assertEquals(newTitle, updatedNote.title)
        assertEquals(newText, updatedNote.text)
    }

    @Test
    fun updateTitle() = runTest {
        val id: Long = 2
        val newTitle = "new title"
        updateTitleUseCase!!.invoke(id, newTitle)
        val updatedNote = noteDAO!!.load(id)
        assertEquals(newTitle, updatedNote.title)
    }

    @Test
    fun loadNote() = runTest {
        val id: Long = 2
        val exp = notes.find { it.id == id }
        val act = noteDAO!!.load(id).dbo
        assertEquals(exp, act)
    }

    /**
     * WasmJs-specific implementation of runTest with mutex synchronization.
     * 
     * On WasmJs (WebAssembly JavaScript), coroutines behave differently than on JVM/Android/iOS platforms.
     * The web platform's single-threaded nature combined with asynchronous database operations
     * can cause race conditions and test failures. This implementation uses a Mutex to ensure
     * that tests run sequentially, preventing:
     * - Database schema creation conflicts
     * - Concurrent test data insertion issues
     * - Race conditions in WebWorkerDriver operations
     * 
     * The mutex ensures that only one test runs at a time, providing the same deterministic
     * behavior across all platforms while handling WasmJs's asynchronous execution model.
     */
    private fun runTest(
        context: CoroutineContext = EmptyCoroutineContext,
        timeout: Duration = 60.seconds,
        testBody: suspend TestScope.() -> Unit
    ): TestResult = kotlinx.coroutines.test.runTest(context, timeout) {
        mutex.withLock { testBody() }
    }
}