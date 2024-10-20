package com.softartdev.notedelight.shared.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import kotlinx.coroutines.launch

class SaveViewModel(
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {

    fun saveNoteAndNavBack() = viewModelScope.launch(context = coroutineDispatchers.default) {
        SaveNoteUseCase.dialogChannel.send(true)
        navigateUp()
    }

    fun doNotSaveAndNavBack() = viewModelScope.launch(context = coroutineDispatchers.default) {
        SaveNoteUseCase.dialogChannel.send(false)
        navigateUp()
    }

    fun navigateUp() = viewModelScope.launch(context = coroutineDispatchers.main) {
        router.popBackStack()
    }
}