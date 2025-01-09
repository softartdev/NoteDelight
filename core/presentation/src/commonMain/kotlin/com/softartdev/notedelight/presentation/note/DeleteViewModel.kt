package com.softartdev.notedelight.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.usecase.note.DeleteNoteUseCase
import kotlinx.coroutines.launch

class DeleteViewModel(private val router: Router) : ViewModel() {

    fun deleteNoteAndNavBack() = viewModelScope.launch {
        DeleteNoteUseCase.deleteChannel.send(true)
        router.popBackStack()
    }

    fun doNotDeleteAndNavBack() = viewModelScope.launch {
        DeleteNoteUseCase.deleteChannel.send(false)
        router.popBackStack()
    }

    fun navigateUp() = router.popBackStack()
}