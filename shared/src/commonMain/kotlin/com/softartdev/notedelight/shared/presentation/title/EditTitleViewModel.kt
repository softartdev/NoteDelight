package com.softartdev.notedelight.shared.presentation.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditTitleViewModel(
    private val noteDAO: NoteDAO,
    private val updateTitleUseCase: UpdateTitleUseCase,
    private val router: Router,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<EditTitleResult> = MutableStateFlow(
        value = EditTitleResult.Loading
    )
    val stateFlow: StateFlow<EditTitleResult> = mutableStateFlow

    fun loadTitle(noteId: Long) = viewModelScope.launch {
        mutableStateFlow.value = EditTitleResult.Loading
        try {
            val note = noteDAO.load(noteId)
            mutableStateFlow.value = EditTitleResult.Loaded(note.title)
        } catch (t: Throwable) {
            Napier.e("❌", t)
            mutableStateFlow.value = EditTitleResult.Error(message = t.message)
        }
    }

    fun editTitle(id: Long, newTitle: String) = viewModelScope.launch {
        mutableStateFlow.value = EditTitleResult.Loading
        try {
            val noteTitle = newTitle.trim()
            if (noteTitle.isEmpty()) {
                mutableStateFlow.value = EditTitleResult.EmptyTitleError
            } else {
                updateTitleUseCase(id, noteTitle)
                UpdateTitleUseCase.titleChannel.send(noteTitle)
                navigateUp()
            }
        } catch (t: Throwable) {
            Napier.e("❌", t)
            mutableStateFlow.value = EditTitleResult.Error(message = t.message)
        }
    }

    fun navigateUp() = router.popBackStack()
}
