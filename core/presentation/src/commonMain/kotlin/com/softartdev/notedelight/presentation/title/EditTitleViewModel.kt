package com.softartdev.notedelight.presentation.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.interactor.SnackbarInteractor
import com.softartdev.notedelight.interactor.SnackbarMessage
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditTitleViewModel(
    private val noteId: Long,
    private val noteDAO: NoteDAO,
    private val updateTitleUseCase: UpdateTitleUseCase,
    private val snackbarInteractor: SnackbarInteractor,
    private val router: Router,
) : ViewModel() {
    private val logger = Logger.withTag(this@EditTitleViewModel::class.simpleName.toString())

    val stateFlow: StateFlow<EditTitleResult>
        field = MutableStateFlow(value = EditTitleResult())

    fun onAction(action: EditTitleAction) = when (action) {
        is EditTitleAction.Cancel -> cancel()
        is EditTitleAction.OnEditTitle -> onEditTitle(action.title)
        is EditTitleAction.OnEditClick -> editTitle()
    }

    fun loadTitle() = viewModelScope.launch {
        stateFlow.update(EditTitleResult::showLoading)
        try {
            val note = noteDAO.load(noteId)
            stateFlow.update { it.copy(title = note.title) }
        } catch (e: Throwable) {
            logger.e(e) { "Error loading note title" }
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            stateFlow.update(EditTitleResult::hideLoading)
        }
    }

    private fun onEditTitle(newTitle: String) = viewModelScope.launch {
        stateFlow.update(EditTitleResult::hideError)
        stateFlow.update { it.copy(title = newTitle) }
    }

    private fun editTitle() = viewModelScope.launch {
        stateFlow.update(EditTitleResult::showLoading)
        try {
            val noteTitle: String = stateFlow.value.title.trim()
            if (noteTitle.isEmpty()) {
                stateFlow.update(EditTitleResult::showError)
            } else {
                stateFlow.update(EditTitleResult::hideError)
                updateTitleUseCase(noteId, noteTitle)
                UpdateTitleUseCase.dialogChannel.send(noteTitle)
                router.popBackStack()
            }
        } catch (e: Throwable) {
            logger.e(e) { "Error updating note title" }
            e.message?.let { snackbarInteractor.showMessage(SnackbarMessage.Simple(it)) }
        } finally {
            stateFlow.update(EditTitleResult::hideLoading)
        }
    }

    private fun cancel() = viewModelScope.launch {
        UpdateTitleUseCase.dialogChannel.send(null)
        router.popBackStack()
    }
}
