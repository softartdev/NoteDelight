package com.softartdev.notedelight.shared.presentation.title

sealed class EditTitleResult {
    data object Loading: EditTitleResult()
    data class Loaded(val title: String): EditTitleResult()
    data object EmptyTitleError: EditTitleResult()
    data class Error(val message: String?): EditTitleResult()
}