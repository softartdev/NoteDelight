@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.softartdev.notedelight.ui.screen.MainTestScreen
import com.softartdev.notedelight.ui.screen.NoteScreen
import com.softartdev.notedelight.ui.screen.SettingsTestScreen
import com.softartdev.notedelight.ui.screen.SignInScreen
import com.softartdev.notedelight.ui.screen.dialog.ChangePasswordDialog
import com.softartdev.notedelight.ui.screen.dialog.CommonDialog
import com.softartdev.notedelight.ui.screen.dialog.CommonDialogImpl
import com.softartdev.notedelight.ui.screen.dialog.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.screen.dialog.EditTitleDialog
import com.softartdev.notedelight.ui.screen.dialog.EnterPasswordDialog
import com.softartdev.notedelight.ui.screen.dialog.LanguageDialog

abstract class BaseTestCase(val composeUiTest: ComposeUiTest) {

    private val commonDialog: CommonDialog = CommonDialogImpl(composeUiTest)

    suspend fun signInScreen(block: suspend SignInScreen.() -> Unit) =
        SignInScreen(composeUiTest).block()

    suspend fun mainTestScreen(block: suspend MainTestScreen.() -> Unit) =
        MainTestScreen(composeUiTest).block()

    suspend fun noteScreen(block: suspend NoteScreen.() -> Unit) =
        NoteScreen(composeUiTest).block()

    suspend fun settingsTestScreen(block: suspend SettingsTestScreen.() -> Unit) =
        SettingsTestScreen(composeUiTest).block()

    suspend fun commonDialog(block: suspend CommonDialog.() -> Unit) =
        commonDialog.block()

    suspend fun editTitleDialog(block: suspend EditTitleDialog.() -> Unit) =
        EditTitleDialog(commonDialog).block()

    suspend fun confirmPasswordDialog(block: suspend ConfirmPasswordDialog.() -> Unit) =
        ConfirmPasswordDialog(commonDialog).block()

    suspend fun enterPasswordDialog(block: suspend EnterPasswordDialog.() -> Unit) =
        EnterPasswordDialog(commonDialog).block()

    suspend fun changePasswordDialog(block: suspend ChangePasswordDialog.() -> Unit) =
        ChangePasswordDialog(commonDialog).block()

    suspend fun languageDialog(block: suspend LanguageDialog.() -> Unit) =
        LanguageDialog(commonDialog).block()
}
