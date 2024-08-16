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

    fun argRoute(message: String?): String = when (message) {
        null -> name
        else -> "$name?message=$message"
    }
}