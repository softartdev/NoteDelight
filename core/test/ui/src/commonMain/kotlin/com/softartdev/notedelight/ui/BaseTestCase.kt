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
import com.softartdev.notedelight.ui.screen.dialog.SaveDialog

abstract class BaseTestCase(val composeUiTest: ComposeUiTest) {

    val commonDialog: CommonDialog = CommonDialogImpl(composeUiTest)

    suspend inline fun signInScreen(block: suspend SignInScreen.() -> Unit) =
        SignInScreen(composeUiTest).block()

    suspend inline fun mainTestScreen(block: suspend MainTestScreen.() -> Unit) =
        MainTestScreen(composeUiTest).block()

    suspend inline fun noteScreen(block: suspend NoteScreen.() -> Unit) =
        NoteScreen(composeUiTest).block()

    suspend inline fun settingsTestScreen(block: suspend SettingsTestScreen.() -> Unit) =
        SettingsTestScreen(composeUiTest).block()

    suspend inline fun commonDialog(block: suspend CommonDialog.() -> Unit) =
        commonDialog.block()

    suspend inline fun editTitleDialog(block: suspend EditTitleDialog.() -> Unit) =
        EditTitleDialog(commonDialog).block()

    suspend inline fun saveDialog(block: suspend SaveDialog.() -> Unit) =
        SaveDialog(commonDialog).block()

    suspend inline fun confirmPasswordDialog(block: suspend ConfirmPasswordDialog.() -> Unit) =
        ConfirmPasswordDialog(commonDialog).block()

    suspend inline fun enterPasswordDialog(block: suspend EnterPasswordDialog.() -> Unit) =
        EnterPasswordDialog(commonDialog).block()

    suspend inline fun changePasswordDialog(block: suspend ChangePasswordDialog.() -> Unit) =
        ChangePasswordDialog(commonDialog).block()

    suspend inline fun languageDialog(block: suspend LanguageDialog.() -> Unit) =
        LanguageDialog(commonDialog).block()
}
