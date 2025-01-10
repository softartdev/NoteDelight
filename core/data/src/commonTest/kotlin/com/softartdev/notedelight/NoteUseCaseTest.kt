package com.softartdev.notedelight

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
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class NoteUseCaseTest : BaseTest() {

    private val noteDAO = safeRepo.noteDAO
    private val createNoteUseCase = CreateNoteUseCase(noteDAO)
    private val saveNoteUseCase = SaveNoteUseCase(noteDAO)
    private val updateTitleUseCase = UpdateTitleUseCase(noteDAO)

    private val notes = listOf(TestSchema.firstNote, TestSchema.secondNote, TestSchema.thirdNote)

    @BeforeTest
    fun setUp() = runTest {
        noteDB.noteQueries.transaction {
            notes.forEach(noteDB.noteQueries::update)
        }
    }

    @AfterTest
    fun tearDown() = runTest {
        noteDB.noteQueries.deleteAll()
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
        assertContentEquals(notes.toModel(), noteDAO.listFlow.first())
    }

    @Test
    fun createNote() = runTest {
        val lastId = notes.maxByOrNull(Note::id)?.id ?: 0
        val newId = lastId + 1
        assertEquals(newId, createNoteUseCase.invoke())
    }

    @Test
    fun saveNote() = runTest {
        val id: Long = 2
        val newTitle = "new title"
        val newText = "new text"
        saveNoteUseCase(id, newTitle, newText)
        val updatedNote = noteDAO.load(id)
        assertEquals(newTitle, updatedNote.title)
        assertEquals(newText, updatedNote.text)
    }

    @Test
    fun updateTitle() = runTest {
        val id: Long = 2
        val newTitle = "new title"
        updateTitleUseCase(id, newTitle)
        val updatedNote = noteDAO.load(id)
        assertEquals(newTitle, updatedNote.title)
    }

    @Test
    fun loadNote() = runTest {
        val id: Long = 2
        val exp = notes.find { it.id == id }
        val act = noteDAO.load(id).dbo
        assertEquals(exp, act)
    }
}