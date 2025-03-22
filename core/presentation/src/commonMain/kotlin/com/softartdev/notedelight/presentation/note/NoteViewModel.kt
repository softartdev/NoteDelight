package com.softartdev.notedelight.presentation.note

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(
    private var noteId: Long,
    private val noteDAO: NoteDAO,
    private val createNoteUseCase: CreateNoteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<NoteResult> = MutableStateFlow(
        value = NoteResult(
            onSaveClick = this@NoteViewModel::saveNote,
            onEditClick = this@NoteViewModel::editTitle,
            onDeleteClick = this@NoteViewModel::subscribeToDeleteNote,
            checkSaveChange = this@NoteViewModel::checkSaveChange,
            disposeOneTimeEvents = this@NoteViewModel::disposeOneTimeEvents
        )
    )
    val stateFlow: StateFlow<NoteResult> = mutableStateFlow

    fun createOrLoadNote() = when (noteId) {
        0L -> createNote()
        else -> loadNote(noteId)
    }

    private fun createNote() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            noteId = withContext(coroutineDispatchers.io) {
                createNoteUseCase()
            }
            Napier.d("Created note with id=$noteId")
            loadNote(noteId)
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun loadNote(id: Long) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val note = withContext(coroutineDispatchers.io) {
                noteDAO.load(id)
            }
            Napier.d("Loaded note with id = $noteId")
            mutableStateFlow.update { result -> result.copy(note = note) }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun saveNote(title: String?, text: String) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            if (title.isNullOrEmpty() && text.isEmpty()) {
                mutableStateFlow.update { result: NoteResult ->
                    result.copy(snackBarMessageType = NoteResult.SnackBarMessageType.EMPTY)
                }
            } else {
                val noteTitle: String = withContext(coroutineDispatchers.default) {
                    createTitleIfNeed(title, text)
                }
                withContext(coroutineDispatchers.io) {
                    saveNoteUseCase(noteId, noteTitle, text)
                }
                Napier.d("Saved note with id=$noteId")
                mutableStateFlow.update { result: NoteResult ->
                    result.copy(
                        note = result.note?.copy(title = noteTitle, text = text),
                        snackBarMessageType = NoteResult.SnackBarMessageType.SAVED
                    )
                }
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun editTitle() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            subscribeToEditTitle()
            router.navigate(route = AppNavGraph.EditTitleDialog(noteId = noteId))
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun checkSaveChange(title: String?, text: String) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val noteTitle: String = createTitleIfNeed(title, text)
            mutableStateFlow.update { result: NoteResult ->
                result.copy(note = result.note?.copy(title = noteTitle, text = text))
            }
            val changed: Boolean = isChanged(noteId, noteTitle, text)
            val empty: Boolean = isEmpty(noteId)
            when {
                changed -> {
                    router.navigate(route = AppNavGraph.SaveChangesDialog)
                    subscribeToSaveNote(title, text)
                }
                empty -> deleteNoteForResult()
                else -> router.popBackStack()
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun subscribeToSaveNote(title: String?, text: String) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            Napier.d("Subscribe to save note dialog channel")
            val doSave: Boolean? = withContext(coroutineDispatchers.io) {
                SaveNoteUseCase.dialogChannel.receive()
            }
            when (doSave) {
                null -> Napier.d("Cancel")
                true -> saveNoteAndNavBack(title, text)
                false -> doNotSaveAndNavBack()
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun saveNoteAndNavBack(title: String?, text: String) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val noteTitle: String = createTitleIfNeed(title, text)
            saveNoteUseCase(noteId, noteTitle, text)
            Napier.d("Saved and nav back")
            router.navigateClearingBackStack(route = AppNavGraph.Main) // FIXME
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun doNotSaveAndNavBack() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val noteIsEmpty: Boolean = isEmpty(noteId)
            if (noteIsEmpty) {
                deleteNoteForResult()
            } else {
                Napier.d("Don't save and nav back")
                router.popBackStack()
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun subscribeToDeleteNote() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            router.navigate(route = AppNavGraph.DeleteNoteDialog)
            val doDelete: Boolean = withContext(coroutineDispatchers.io) {
                DeleteNoteUseCase.deleteChannel.receive()
            }
            if (doDelete) {
                deleteNoteForResult()
            } else {
                Napier.d("Don't delete note")
                router.popBackStack()
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private suspend fun deleteNoteForResult() {
        withContext(coroutineDispatchers.io) {
            deleteNoteUseCase.invoke(id = noteId)
        }
        Napier.d("Deleted note with id=$noteId")
        mutableStateFlow.update { result: NoteResult ->
            result.copy(snackBarMessageType = NoteResult.SnackBarMessageType.DELETED)
        }
        router.popBackStack(route = AppNavGraph.Main, inclusive = false, saveState = false)
    }

    private fun subscribeToEditTitle() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val title: String? = withContext(coroutineDispatchers.io) {
                UpdateTitleUseCase.dialogChannel.receive()
            }
            if (title.isNullOrEmpty()) return@launch

            mutableStateFlow.update { result: NoteResult ->
                val updatedNote = result.note?.copy(title = title)
                result.copy(note = updatedNote)
            }
        } catch (e: Throwable) {
            Napier.e("❌", e)
            router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
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

    private fun disposeOneTimeEvents() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::hideSnackBarMessage)
    }

    @VisibleForTesting
    fun resetResultState(noteId: Long = 0L) = mutableStateFlow.update { noteResult ->
        this@NoteViewModel.noteId = noteId
        return@update noteResult.copy(loading = false, note = null, snackBarMessageType = null)
    }
}
