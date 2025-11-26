package com.softartdev.notedelight.presentation.note

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.interactor.SnackbarTextResource
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.usecase.note.DeleteNoteUseCase
import com.softartdev.notedelight.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(
    private val adaptiveInteractor: AdaptiveInteractor,
    private val noteDAO: NoteDAO,
    private val createNoteUseCase: CreateNoteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val logger = Logger.withTag(this@NoteViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<NoteResult> = MutableStateFlow(NoteResult())
    val stateFlow: StateFlow<NoteResult> = mutableStateFlow

    private var noteId: Long
        set(value) { adaptiveInteractor.selectedNoteIdStateFlow.value = value }
        get() = requireNotNull(adaptiveInteractor.selectedNoteIdStateFlow.value)

    var job: Job? = null

    fun launchCollectingSelectedNoteId() {
        if (job != null) return
        job = viewModelScope.launch {
            adaptiveInteractor.selectedNoteIdStateFlow.collect { selectedNoteId: Long? ->
                logger.d { "Collected note id = $selectedNoteId" }
                when (selectedNoteId) {
                    null -> mutableStateFlow.update { result -> result.copy(note = null) }
                    else -> createOrLoadNote()
                }
            }
        }
    }

    fun onAction(action: NoteAction) = when (action) {
        is NoteAction.Save -> saveNote(title = action.title, text = action.text)
        is NoteAction.Edit -> editTitle()
        is NoteAction.Delete -> subscribeToDeleteNote()
        is NoteAction.CheckSaveChange -> checkSaveChange(title = action.title, text = action.text)
    }

    private fun createOrLoadNote() = when (noteId) {
        0L -> createNote()
        else -> loadNote()
    }

    private fun createNote() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val id: Long = withContext(coroutineDispatchers.io) {
                createNoteUseCase()
            }
            logger.d { "Created note with id=$id" }
            noteId = id
        } catch (e: Throwable) {
            handleError(e) { "Error creating note" }
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun loadNote() = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val note = withContext(coroutineDispatchers.io) {
                noteDAO.load(noteId)
            }
            logger.d { "Loaded note with id = $noteId" }
            mutableStateFlow.update { result -> result.copy(note = note) }
        } catch (e: Throwable) {
            handleError(e) { "Error loading note" }
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun saveNote(title: String?, text: String) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            if (title.isNullOrEmpty() && text.isEmpty()) {
                snackbarInteractor.showMessage(SnackbarMessage.Resource(SnackbarTextResource.EMPTY))
            } else {
                val noteTitle: String = withContext(coroutineDispatchers.default) {
                    createTitleIfNeed(title, text)
                }
                withContext(coroutineDispatchers.io) {
                    saveNoteUseCase(noteId, noteTitle, text)
                }
                logger.d { "Saved note with id=$noteId" }
                mutableStateFlow.update { result: NoteResult ->
                    result.copy(
                        note = result.note?.copy(title = noteTitle, text = text)
                    )
                }
                snackbarInteractor.showMessage(SnackbarMessage.Resource(
                    res = SnackbarTextResource.SAVED,
                    suffix = noteTitle
                ))
            }
        } catch (e: Throwable) {
            handleError(e) { "Error saving note" }
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
            handleError(e) { "Error navigating to edit title dialog" }
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
                else -> adaptiveNavigateBack()
            }
        } catch (e: Throwable) {
            handleError(e) { "Error checking save changes" }
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun subscribeToSaveNote(title: String?, text: String) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            logger.d { "Subscribe to save note dialog channel" }
            val doSave: Boolean? = withContext(coroutineDispatchers.io) {
                SaveNoteUseCase.dialogChannel.receive()
            }
            when (doSave) {
                null -> logger.d { "Cancel" }
                true -> saveNoteAndNavBack(title, text)
                false -> doNotSaveAndNavBack()
            }
        } catch (e: Throwable) {
            handleError(e) { "Error subscribing to save note dialog channel" }
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun saveNoteAndNavBack(title: String?, text: String) = viewModelScope.launch {
        mutableStateFlow.update(NoteResult::showLoading)
        try {
            val noteTitle: String = createTitleIfNeed(title, text)
            saveNoteUseCase(noteId, noteTitle, text)
            logger.d { "Saved and nav back" }
            adaptiveNavigateBack()
        } catch (e: Throwable) {
            handleError(e) { "Error saving note and navigating back" }
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
                logger.d { "Don't save and nav back" }
                adaptiveNavigateBack()
            }
        } catch (e: Throwable) {
            handleError(e) { "Error not saving note and navigating back" }
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
                logger.d { "Don't delete note" }
                adaptiveNavigateBack()
            }
        } catch (e: Throwable) {
            handleError(e) { "Error subscribing to delete note dialog channel" }
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private suspend fun deleteNoteForResult() {
        withContext(coroutineDispatchers.io) {
            deleteNoteUseCase.invoke(id = noteId)
        }
        logger.d { "Deleted note with id=$noteId" }
        snackbarInteractor.showMessage(SnackbarMessage.Resource(SnackbarTextResource.DELETED))
        adaptiveNavigateBack()
    }
    
    private suspend fun adaptiveNavigateBack() {
        adaptiveInteractor.selectedNoteIdStateFlow.value = null
        router.adaptiveNavigateBack()
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
            handleError(e) { "Error subscribing to edit title dialog channel" }
        } finally {
            mutableStateFlow.update(NoteResult::hideLoading)
        }
    }

    private fun createTitleIfNeed(title: String?, text: String): String =
        if (title.isNullOrEmpty()) createTitle(text) else title

    private fun createTitle(text: String): String {
        var title = text.take(30.coerceAtMost(text.length))

        if (text.length > 30) {
            val lastSpace: Int = title.lastIndexOf(' ')
            if (lastSpace > 0) {
                title = title.take(lastSpace)
            }
        }
        val firstLine = title.indexOf('\n')
        if (firstLine > 0) {
            title = title.take(firstLine)
        }
        return title
    }

    private suspend fun isChanged(id: Long, title: String, text: String): Boolean {
        val note = noteDAO.load(id)
        return note.title != title || note.text != text
    }

    private suspend fun isEmpty(id: Long): Boolean {
        val note = noteDAO.load(id)
        return note.title.isEmpty() && note.text.isEmpty()
    }

    private inline fun handleError(e: Throwable, messageSupplier: () -> String) {
        logger.e(throwable = e, message = messageSupplier)
        router.navigate(route = AppNavGraph.ErrorDialog(message = e.message))
    }

    @VisibleForTesting
    fun resetResultState(noteId: Long = 0L) = mutableStateFlow.update { noteResult ->
        this@NoteViewModel.noteId = noteId
        return@update noteResult.copy(loading = false, note = null)
    }
}
