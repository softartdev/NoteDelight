package com.softartdev.notedelight.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SaveViewModel(
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {

    fun saveNoteAndNavBack() = viewModelScope.launch {
        withContext(coroutineDispatchers.io) {
            SaveNoteUseCase.dialogChannel.send(true)
        }
        router.popBackStack()
    }

    fun doNotSaveAndNavBack() = viewModelScope.launch {
        withContext(coroutineDispatchers.io) {
            SaveNoteUseCase.dialogChannel.send(false)
        }
        router.popBackStack()
    }

    fun navigateUp() = viewModelScope.launch {
        withContext(coroutineDispatchers.io) {
            SaveNoteUseCase.dialogChannel.send(null)
        }
        router.popBackStack()
    }
}