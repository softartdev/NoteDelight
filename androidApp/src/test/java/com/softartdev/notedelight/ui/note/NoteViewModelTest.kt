package com.softartdev.notedelight.ui.note

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.Note
import com.softartdev.notedelight.shared.test.util.MainCoroutineRule
import com.softartdev.notedelight.shared.test.util.assertValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val noteUseCase = Mockito.mock(NoteUseCase::class.java)
    private var noteViewModel = NoteViewModel(noteUseCase)

    private val id = 1L
    private val title: String = "title"
    private val text: String = "text"
    private val date: Date = Date()
    private val note = Note(id, title, text, date, date)
    private val titleChannel = Channel<String>()

    @Before
    fun setUp() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.createNote()).thenReturn(id)
        Mockito.`when`(noteUseCase.loadNote(id)).thenReturn(note)
        Mockito.`when`(noteUseCase.saveNote(id, title, text)).thenReturn(1)
        Mockito.`when`(noteUseCase.titleChannel).thenReturn(titleChannel)
        Mockito.`when`(noteUseCase.deleteNote(id)).thenReturn(1)
    }

    @After
    fun tearDown() = mainCoroutineRule.runBlockingTest {
        noteViewModel.resultLiveData.value = null
    }

    @Test
    fun createNote() = noteViewModel.resultLiveData.assertValues(
            NoteResult.Loading,
            NoteResult.Created(id)
    ) {
        noteViewModel.createNote()
    }

    @Test
    fun loadNote() = noteViewModel.resultLiveData.assertValues(
            NoteResult.Loading,
            NoteResult.Loaded(note)
    ) {
        noteViewModel.loadNote(id)
    }

    @Test
    fun saveNoteEmpty() = noteViewModel.resultLiveData.assertValues(
            NoteResult.Loading,
            NoteResult.Empty
    ) {
        noteViewModel.saveNote("", "")
    }

    @Test
    fun saveNote() = noteViewModel.resultLiveData.assertValues(
            NoteResult.Loading,
            NoteResult.Saved(title)
    ) {
        noteViewModel.setIdForTest(id)
        noteViewModel.saveNote(title, text)
    }

    @Test
    fun editTitle() = noteViewModel.resultLiveData.assertValues(
            NoteResult.Loading,
            NoteResult.NavEditTitle(id),
            NoteResult.TitleUpdated(title)
    ) {
        noteViewModel.setIdForTest(id)
        noteViewModel.editTitle()
        runBlocking { titleChannel.send(title) }
    }

    @Test
    fun deleteNote() = noteViewModel.resultLiveData.assertValues(
            NoteResult.Loading,
            NoteResult.Deleted
    ) {
        noteViewModel.setIdForTest(id)
        noteViewModel.deleteNote()
    }

    @Test
    fun checkSaveChange() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.isChanged(id, title, text)).thenReturn(true)
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(false)
        noteViewModel.resultLiveData.assertValues(
                NoteResult.Loading,
                NoteResult.CheckSaveChange
        ) {
            noteViewModel.setIdForTest(id)
            noteViewModel.checkSaveChange(title, text)
        }
    }

    @Test
    fun checkSaveChangeNavBack() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.isChanged(id, title, text)).thenReturn(false)
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(false)
        noteViewModel.resultLiveData.assertValues(
                NoteResult.Loading,
                NoteResult.NavBack
        ) {
            noteViewModel.setIdForTest(id)
            noteViewModel.checkSaveChange(title, text)
        }
    }

    @Test
    fun checkSaveChangeDeleted() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.isChanged(id, title, text)).thenReturn(false)
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(true)
        noteViewModel.resultLiveData.assertValues(
                NoteResult.Loading,
                NoteResult.Deleted
        ) {
            noteViewModel.setIdForTest(id)
            noteViewModel.checkSaveChange(title, text)
        }
    }

    @Test
    fun saveNoteAndNavBack() = noteViewModel.resultLiveData.assertValues(
            NoteResult.Loading,
            NoteResult.NavBack
    ) {
        noteViewModel.setIdForTest(id)
        noteViewModel.saveNoteAndNavBack(title, text)
    }

    @Test
    fun doNotSaveAndNavBack() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(false)
        noteViewModel.resultLiveData.assertValues(
                NoteResult.Loading,
                NoteResult.NavBack
        ) {
            noteViewModel.setIdForTest(id)
            noteViewModel.doNotSaveAndNavBack()
        }
    }

    @Test
    fun doNotSaveAndNavBackDeleted() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(noteUseCase.isEmpty(id)).thenReturn(true)
        noteViewModel.resultLiveData.assertValues(
                NoteResult.Loading,
                NoteResult.Deleted
        ) {
            noteViewModel.setIdForTest(id)
            noteViewModel.doNotSaveAndNavBack()
        }
    }

    @Test
    fun errorResult() {
        assertEquals(NoteResult.Error(null), noteViewModel.errorResult(Throwable()))
    }
}