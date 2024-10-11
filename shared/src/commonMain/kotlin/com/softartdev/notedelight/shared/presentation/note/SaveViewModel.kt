package com.softartdev.notedelight.shared.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class SaveViewModel(private val router: Router) : ViewModel() {

    fun saveNoteAndNavBack() = viewModelScope.launch(context = Dispatchers.IO) {
        SaveNoteUseCase.saveChannel.send(true)
        navigateUp()
    }

    fun doNotSaveAndNavBack() = viewModelScope.launch(context = Dispatchers.IO) {
        SaveNoteUseCase.saveChannel.send(false)
        navigateUp()
    }

    fun navigateUp() = viewModelScope.launch(context = Dispatchers.Main) {
        router.popBackStack()
    }
}