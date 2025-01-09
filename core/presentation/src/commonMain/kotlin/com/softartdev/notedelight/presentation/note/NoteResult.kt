package com.softartdev.notedelight.presentation.note

import com.softartdev.notedelight.model.Note

data class NoteResult(
    val loading: Boolean = false,
    val note: Note? = null,
    val snackBarMessageType: SnackBarMessageType? = null,
    val onSaveClick: (title: String?, text: String) -> Unit = { _, _ -> },
    val onEditClick: () -> Unit = {},
    val onDeleteClick: () -> Unit = {},
    val checkSaveChange: (title: String, text: String) -> Unit = { _, _ -> },
    val disposeOneTimeEvents: () -> Unit = {},
) {
    enum class SnackBarMessageType { SAVED, EMPTY, DELETED }

    fun showLoading(): NoteResult = copy(loading = true)
    fun hideLoading(): NoteResult = copy(loading = false)
    fun hideSnackBarMessage(): NoteResult = copy(snackBarMessageType = null)
}
