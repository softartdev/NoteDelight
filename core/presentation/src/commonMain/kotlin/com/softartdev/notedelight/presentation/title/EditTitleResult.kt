package com.softartdev.notedelight.presentation.title

data class EditTitleResult(
    val loading: Boolean = false,
    val title: String = "",
    val isError: Boolean = false,
    val snackBarMessageType: String? = null,
) {
    fun showLoading(): EditTitleResult = copy(loading = true)
    fun hideLoading(): EditTitleResult = copy(loading = false)
    fun showError(): EditTitleResult = copy(isError = true)
    fun hideError(): EditTitleResult = copy(isError = false)
    fun hideSnackBarMessage(): EditTitleResult = copy(snackBarMessageType = null)
}

sealed interface EditTitleAction {
    data object Cancel : EditTitleAction
    data class OnEditTitle(val title: String) : EditTitleAction
    data object OnEditClick : EditTitleAction
}
