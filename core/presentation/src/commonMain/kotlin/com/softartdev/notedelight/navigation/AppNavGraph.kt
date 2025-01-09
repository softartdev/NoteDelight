package com.softartdev.notedelight.navigation

import kotlinx.serialization.Serializable

sealed interface AppNavGraph {

    @Serializable
    data object Splash : AppNavGraph

    @Serializable
    data object SignIn : AppNavGraph

    @Serializable
    data object Main : AppNavGraph

    @Serializable
    data class Details(val noteId: Long) : AppNavGraph

    @Serializable
    data object Settings : AppNavGraph

    @Serializable
    data object ThemeDialog : AppNavGraph

    @Serializable
    data object SaveChangesDialog : AppNavGraph

    @Serializable
    data class EditTitleDialog(val noteId: Long) : AppNavGraph

    @Serializable
    data object DeleteNoteDialog : AppNavGraph

    @Serializable
    data object EnterPasswordDialog : AppNavGraph

    @Serializable
    data object ConfirmPasswordDialog : AppNavGraph

    @Serializable
    data object ChangePasswordDialog : AppNavGraph

    @Serializable
    data class ErrorDialog(val message: String?) : AppNavGraph
}
