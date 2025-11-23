package com.softartdev.notedelight.ui

import androidx.compose.ui.test.junit4.ComposeContentTestRule
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

abstract class BaseTestCase(val composeTestRule: ComposeContentTestRule) {

    private val commonDialog: CommonDialog = CommonDialogImpl(composeTestRule)

    fun signInScreen(block: SignInScreen.() -> Unit) = SignInScreen(composeTestRule).block()

    fun mainTestScreen(block: MainTestScreen.() -> Unit) = MainTestScreen(composeTestRule).block()

    fun noteScreen(block: NoteScreen.() -> Unit) = NoteScreen(composeTestRule).block()

    fun settingsTestScreen(block: SettingsTestScreen.() -> Unit) =
        SettingsTestScreen(composeTestRule).block()

    fun commonDialog(block: CommonDialog.() -> Unit) = commonDialog.block()

    fun editTitleDialog(block: EditTitleDialog.() -> Unit) = EditTitleDialog(commonDialog).block()

    fun confirmPasswordDialog(block: ConfirmPasswordDialog.() -> Unit) =
        ConfirmPasswordDialog(commonDialog).block()

    fun enterPasswordDialog(block: EnterPasswordDialog.() -> Unit) =
        EnterPasswordDialog(commonDialog).block()

    fun changePasswordDialog(block: ChangePasswordDialog.() -> Unit) =
        ChangePasswordDialog(commonDialog).block()

    fun languageDialog(block: LanguageDialog.() -> Unit) =
        LanguageDialog(commonDialog).block()
}
