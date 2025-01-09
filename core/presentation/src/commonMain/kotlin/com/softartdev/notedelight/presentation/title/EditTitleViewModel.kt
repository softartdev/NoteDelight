package com.softartdev.notedelight.presentation.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.note.UpdateTitleUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditTitleViewModel(
    private val noteId: Long,
    private val noteDAO: NoteDAO,
    private val updateTitleUseCase: UpdateTitleUseCase,
    private val router: Router,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<EditTitleResult> = MutableStateFlow(
        value = EditTitleResult(
            onCancel = this::cancel,
            onEditClick = this::editTitle,
            onEditTitle = this::onEditTitle,
            disposeOneTimeEvents = this::disposeOneTimeEvents
        )
    )
    val stateFlow: StateFlow<EditTitleResult> = mutableStateFlow

    fun loadTitle() = viewModelScope.launch {
        mutableStateFlow.update(EditTitleResult::showLoading)
        try {
            val note = noteDAO.load(noteId)
            mutableStateFlow.update { it.copy(title = note.title) }
        } catch (t: Throwable) {
            Napier.e("❌", t)
            mutableStateFlow.update { it.copy(snackBarMessageType = t.message) }
        } finally {
            mutableStateFlow.update(EditTitleResult::hideLoading)
        }
    }

    private fun onEditTitle(newTitle: String) = viewModelScope.launch {
        mutableStateFlow.update(EditTitleResult::hideError)
        mutableStateFlow.update { it.copy(title = newTitle) }
    }

    private fun editTitle() = viewModelScope.launch {
        mutableStateFlow.update(EditTitleResult::showLoading)
        try {
            val noteTitle: String = mutableStateFlow.value.title.trim()
            if (noteTitle.isEmpty()) {
                mutableStateFlow.update(EditTitleResult::showError)
            } else {
                mutableStateFlow.update(EditTitleResult::hideError)
                updateTitleUseCase(noteId, noteTitle)
                UpdateTitleUseCase.dialogChannel.send(noteTitle)
                router.popBackStack()
            }
        } catch (t: Throwable) {
            Napier.e("❌", t)
            mutableStateFlow.update { it.copy(snackBarMessageType = t.message) }
        } finally {
            mutableStateFlow.update(EditTitleResult::hideLoading)
        }
    }

    private fun cancel() = viewModelScope.launch {
        UpdateTitleUseCase.dialogChannel.send(null)
        router.popBackStack()
    }

    private fun disposeOneTimeEvents() = viewModelScope.launch {
        mutableStateFlow.update(EditTitleResult::hideSnackBarMessage)
    }
}
