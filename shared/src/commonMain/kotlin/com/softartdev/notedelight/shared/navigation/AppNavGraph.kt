package com.softartdev.notedelight.shared.navigation

enum class AppNavGraph {
    Splash,
    SignIn,
    Main,
    Details,
    Settings,
    ThemeDialog,
    SaveChangesDialog,
    EditTitleDialog,
    DeleteNoteDialog,
    EnterPasswordDialog,
    ConfirmPasswordDialog,
    ChangePasswordDialog,
    ErrorDialog;

    val route: String
        get() = when (this) {
            Details, EditTitleDialog -> "$name/{$ARG_NOTE_ID}"
            ErrorDialog -> "$name?$ARG_MESSAGE={$ARG_MESSAGE}"
            else -> name
        }

    fun argRoute(message: String?): String = when (message) {
        null -> name
        else -> "$name?$ARG_MESSAGE=$message"
    }

    fun argRoute(noteId: Long): String = "$name/$noteId"

    companion object {
        const val ARG_NOTE_ID = "noteId"
        const val ARG_MESSAGE = "message"
    }
}