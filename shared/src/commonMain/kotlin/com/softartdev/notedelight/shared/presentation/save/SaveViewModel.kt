package com.softartdev.notedelight.shared.presentation.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import kotlinx.coroutines.launch

class SaveViewModel(private val router: Router) : ViewModel() {

    fun saveNoteAndNavBack() = viewModelScope.launch {
        SaveNoteUseCase.saveChannel.send(true)
        router.popBackStack()
    }

    fun doNotSaveAndNavBack() = viewModelScope.launch {
        SaveNoteUseCase.saveChannel.send(false)
        router.popBackStack()
    }

    fun navigateUp() = router.popBackStack()
}