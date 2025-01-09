package com.softartdev.notedelight.presentation.title

data class EditTitleResult(
    val loading: Boolean = false,
    val title: String = "",
    val isError: Boolean = false,
    val snackBarMessageType: String? = null,
    val onCancel: () -> Unit = {},
    val onEditTitle: (title: String) -> Unit = {},
    val onEditClick: () -> Unit = {},
    val disposeOneTimeEvents: () -> Unit = {}
) {
    fun showLoading(): EditTitleResult = copy(loading = true)
    fun hideLoading(): EditTitleResult = copy(loading = false)
    fun showError(): EditTitleResult = copy(isError = true)
    fun hideError(): EditTitleResult = copy(isError = false)
    fun hideSnackBarMessage(): EditTitleResult = copy(snackBarMessageType = null)
}
