package com.softartdev.notedelight.shared.presentation.note

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase
import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(
    private val noteDAO: NoteDAO,
    private val createNoteUseCase: CreateNoteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<NoteResult> = MutableStateFlow(
        value = NoteResult.Loading
    )
    val stateFlow: StateFlow<NoteResult> = mutableStateFlow

    private var noteId: Long = 0
        get() = when (field) {
            0L -> throw IllegalStateException("Note doesn't loaded")
            else -> field
        }

    fun createNote() = viewModelScope.launch {
        noteId = withContext(coroutineDispatchers.io) {
            createNoteUseCase()
        }
        Napier.d("Created note with id=$noteId")
        mutableStateFlow.value = NoteResult.Created(noteId)
    }

    fun loadNote(id: Long) = viewModelScope.launch {
        val note = withContext(coroutineDispatchers.io) {
            noteDAO.load(id)
        }
        noteId = note.id
        Napier.d("Loaded note with id=$noteId")
        mutableStateFlow.value = NoteResult.Loaded(note)
    }

    fun saveNote(title: String?, text: String) = viewModelScope.launch {
        if (title.isNullOrEmpty() && text.isEmpty()) {
            mutableStateFlow.value = NoteResult.Empty
        } else {
            val noteTitle: String = withContext(coroutineDispatchers.default) {
                createTitleIfNeed(title, text)
            }
            withContext(coroutineDispatchers.io) {
                saveNoteUseCase(noteId, noteTitle, text)
            }
            Napier.d("Saved note with id=$noteId")
            mutableStateFlow.value = NoteResult.Saved(noteTitle)
        }
    }

    fun editTitle() = viewModelScope.launch {
        subscribeToEditTitle()
        router.navigate(route = AppNavGraph.EditTitleDialog(noteId = noteId))
    }

    fun deleteNote() = viewModelScope.launch {
        mutableStateFlow.value = deleteNoteForResult()
    }

    fun checkSaveChange(title: String?, text: String) = viewModelScope.launch {
        val noteTitle: String = createTitleIfNeed(title, text)
        val changed: Boolean = isChanged(noteId, noteTitle, text)
        val empty: Boolean = isEmpty(noteId)
        when {
            changed -> {
                router.navigate(route = AppNavGraph.SaveChangesDialog)
                subscribeToSaveNote(title, text)
            }
            empty -> mutableStateFlow.value = deleteNoteForResult()
            else -> router.popBackStack()
        }
    }

    private fun subscribeToSaveNote(title: String?, text: String) = viewModelScope.launch {
        Napier.d("Subscribe to save note dialog channel")
        val doSave: Boolean = withContext(coroutineDispatchers.io) {
            SaveNoteUseCase.dialogChannel.receive()
        }
        when {
            doSave -> saveNoteAndNavBack(title, text)
            else -> doNotSaveAndNavBack()
        }
    }

    fun saveNoteAndNavBack(title: String?, text: String) = viewModelScope.launch {
        val noteTitle: String = createTitleIfNeed(title, text)
        saveNoteUseCase(noteId, noteTitle, text)
        Napier.d("Saved and nav back")
        router.popBackStack()
    }

    fun doNotSaveAndNavBack() = viewModelScope.launch {
        val noteIsEmpty: Boolean = isEmpty(noteId)
        if (noteIsEmpty) {
            mutableStateFlow.value = deleteNoteForResult()
        } else {
            Napier.d("Don't save and nav back")
            router.popBackStack()
        }
    }

    fun subscribeToDeleteNote() = viewModelScope.launch {
        router.navigate(route = AppNavGraph.DeleteNoteDialog)
        val doDelete: Boolean = withContext(coroutineDispatchers.io) {
            DeleteNoteUseCase.deleteChannel.receive()
        }
        if (doDelete) {
            mutableStateFlow.value = deleteNoteForResult()
        } else {
            Napier.d("Don't delete note")
            router.popBackStack()
        }
    }

    private suspend fun deleteNoteForResult(): NoteResult {
        withContext(coroutineDispatchers.io) {
            deleteNoteUseCase.invoke(id = noteId)
        }
        Napier.d("Deleted note with id=$noteId")
        router.popBackStack(route = AppNavGraph.Main, inclusive = false, saveState = false)
        return NoteResult.Deleted
    }

    private fun subscribeToEditTitle() = viewModelScope.launch {
        mutableStateFlow.value = NoteResult.Loading
        try {
            val title: String = withContext(coroutineDispatchers.io) {
                UpdateTitleUseCase.titleChannel.receive()
            }
            mutableStateFlow.value = NoteResult.TitleUpdated(title)
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        }
    }

    public override fun onCleared() {
        super.onCleared()
        // workaround due to koin uses remember function of compose
        mutableStateFlow.value = NoteResult.Loading
    }

    @VisibleForTesting
    fun setIdForTest(id: Long) {
        noteId = id
    }

    private fun createTitleIfNeed(title: String?, text: String): String =
        if (title.isNullOrEmpty()) createTitle(text) else title

    private fun createTitle(text: String): String {
        var title = text.substring(0, 30.coerceAtMost(text.length))

        if (text.length > 30) {
            val lastSpace: Int = title.lastIndexOf(' ')
            if (lastSpace > 0) {
                title = title.substring(0, lastSpace)
            }
        }
        val firstLine = title.indexOf('\n')
        if (firstLine > 0) {
            title = title.substring(0, firstLine)
        }
        return title
    }

    private fun isChanged(id: Long, title: String, text: String): Boolean {
        val note = noteDAO.load(id)
        return note.title != title || note.text != text
    }

    private fun isEmpty(id: Long): Boolean {
        val note = noteDAO.load(id)
        return note.title.isEmpty() && note.text.isEmpty()
    }
}
