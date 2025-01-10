package com.softartdev.notedelight

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.softartdev.notedelight.db.AndroidDatabaseHolder
import com.softartdev.notedelight.db.NoteDb
import com.softartdev.notedelight.db.SqlDelightNoteDAO
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.db.createQueryWrapper
import com.softartdev.notedelight.db.dbo
import com.softartdev.notedelight.db.toModel
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class NoteUseCaseUnitTest {
    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val mockDbHolder = Mockito.mock(AndroidDatabaseHolder::class.java)

    private val noteDb = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).let { driver ->
        NoteDb.Schema.create(driver)
        return@let createQueryWrapper(driver)
    }
    private val noteDAO = SqlDelightNoteDAO(noteDb.noteQueries)
    private val createNoteUseCase = CreateNoteUseCase(noteDAO)
    private val saveNoteUseCase = SaveNoteUseCase(noteDAO)
    private val deleteNoteUseCase = DeleteNoteUseCase(noteDAO)
    private val updateTitleUseCase = UpdateTitleUseCase(noteDAO)

    @Before
    fun setUp() = runTest {
        Napier.base(PrintAntilog())
        TestSchema.notes.forEach(noteDb.noteQueries::insert)
        Mockito.`when`(mockSafeRepo.buildDbIfNeed()).thenReturn(mockDbHolder)
        Mockito.`when`(mockDbHolder.noteDb).thenReturn(noteDb)
    }

    @After
    fun tearDown() = runTest {
        noteDb.noteQueries.deleteAll()
        Napier.takeLogarithm()
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
        assertEquals(TestSchema.notes.toModel(), noteDAO.listFlow.first())
    }

    @Test
    fun createNote() = runTest {
        val lastId = TestSchema.notes.maxByOrNull(Note::id)?.id ?: 0
        val newId = lastId + 1
        assertEquals(newId, createNoteUseCase())
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
        val exp = TestSchema.notes.find { it.id == id }
        val act = noteDAO.load(id).dbo
        assertEquals(exp, act)
    }

    @Test(expected = NullPointerException::class)
    fun deleteNote() = runTest {
        val id: Long = 2
        deleteNoteUseCase(id)
        noteDAO.load(id)
    }
}