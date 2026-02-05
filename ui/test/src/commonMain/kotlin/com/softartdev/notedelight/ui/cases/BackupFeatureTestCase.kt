@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.createTempBackupPath
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen
import com.softartdev.notedelight.ui.settings.detail.DatabaseFilePicker
import com.softartdev.notedelight.ui.settings.detail.TestDatabaseFilePicker
import com.softartdev.notedelight.util.CONFIRM_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.util.ENTER_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.waitUntilDisplayed
import com.softartdev.notedelight.waitUntilNotExist
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.incorrect_password
import org.jetbrains.compose.resources.getString
import org.koin.mp.KoinPlatformTools
import kotlin.time.Duration.Companion.minutes

class BackupFeatureTestCase(
    composeUiTest: ComposeUiTest,
    private val pressBack: () -> Unit,
    private val closeSoftKeyboard: () -> Unit,
) : () -> TestResult, BaseTestCase(composeUiTest) {
    private val logger = Logger.withTag("ℹ️BackupFeatureTestCase")

    override fun invoke() = runTest(timeout = 5.minutes) {
        logger.i { "Starting BackupFeatureTestCase" }
        val firstExportPath = createTempBackupPath("backup-one")
        val secondExportPath = createTempBackupPath("backup-two")
        val testDatabaseFilePicker: TestDatabaseFilePicker =
            KoinPlatformTools.defaultContext().get().get(DatabaseFilePicker::class)
        testDatabaseFilePicker.setQueues(
            exportPaths = listOf(firstExportPath, secondExportPath),
            importPaths = listOf(firstExportPath, secondExportPath)
        )
        val firstNotes = listOf("Backup A1", "Backup A2")
        val secondNotes = listOf("Backup B1", "Backup B2")
        val firstPassword = "firstPass123"
        val secondPassword = "secondPass456"
        val incorrectPasswordTitle = getString(Res.string.incorrect_password)
        createNotes(firstNotes)
        enableEncryption(firstPassword)
        exportDatabase()
        deleteNotes(firstNotes)
        disableEncryption(firstPassword)
        createNotes(secondNotes)
        enableEncryption(secondPassword)
        exportDatabase()
        importDatabase(firstPassword, incorrectPasswordTitle)
        assertNotesVisible(firstNotes)
        importDatabase(secondPassword, incorrectPasswordTitle)
        assertNotesVisible(secondNotes)
        logger.i { "Ending BackupFeatureTestCase" }
    }

    private suspend fun createNotes(titles: List<String>) {
        for (title in titles) {
            logger.i { "MainTestScreen: Creating note $title" }
            mainTestScreen {
                composeUiTest.waitUntilDisplayed("createNoteFab", blockSNI = ::fabSNI)
                fabSNI.performClick()
            }
            noteScreen {
                composeUiTest.waitUntilDisplayed("noteTextField", blockSNI = ::textFieldSNI)
                textFieldSNI.performTextInput(title)
                closeSoftKeyboard()
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            mainTestScreen {
                composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            }
        }
    }

    private suspend fun enableEncryption(password: String) {
        logger.i { "SettingsTestScreen: Enabling encryption" }
        openSettingsScreen()
        openSecurityCategory()
        settingsTestScreen {
            composeUiTest.waitUntilDisplayed("encryptionSwitch", blockSNI = ::encryptionSwitchSNI)
            encryptionSwitchSNI.performClick()
        }
        confirmPasswordDialog {
            logger.i { "ConfirmPasswordDialog: Setting encryption password" }
            composeUiTest.waitUntilDisplayed("confirmPasswordDialog", blockSNI = ::confirmPasswordSNI)
            confirmPasswordSNI.performTextReplacement(text = password)
            closeSoftKeyboard()
            confirmRepeatPasswordSNI.performTextReplacement(text = password)
            closeSoftKeyboard()
            confirmDialogButtonSNI.performSemanticsAction(SemanticsActions.OnClick)
        }
        composeUiTest.waitUntilNotExist(tag = CONFIRM_PASSWORD_DIALOG_TAG)
        backToMainScreen()
        logger.i { "SettingsTestScreen: Encryption enabled" }
    }

    private suspend fun exportDatabase() {
        logger.i { "SettingsTestScreen: Export database" }
        openSettingsScreen()
        openBackupCategory()
        settingsTestScreen {
            composeUiTest.waitUntilDisplayed("exportDatabase", blockSNI = ::exportDatabaseSNI)
            exportDatabaseSNI.performClick()
        }
        composeUiTest.awaitIdle()
        backToMainScreen()
    }

    private suspend fun deleteNotes(titles: List<String>) {
        for (title in titles) {
            logger.i { "MainTestScreen: Deleting note $title" }
            MainTestScreen.noteItemTitleText = title
            mainTestScreen {
                composeUiTest.waitUntilDisplayed("noteListItem:$title", blockSNI = ::noteListItemSNI)
                noteListItemSNI.performClick()
            }
            noteScreen {
                logger.i { "NoteScreen: Verifying note content for $title" }
                composeUiTest.waitAssert("note text contains $title") {
                    textFieldSNI.assertTextContains(title)
                }
                composeUiTest.waitUntilDisplayed("deleteNoteMenuButton", blockSNI = ::deleteNoteMenuButtonSNI)
                deleteNoteMenuButtonSNI.performClick()
            }
            commonDialog {
                composeUiTest.waitUntilDisplayed("confirmDelete", blockSNI = ::confirmDialogButtonSNI)
                confirmDialogButtonSNI.performSemanticsAction(SemanticsActions.OnClick)
            }
            composeUiTest.waitUntil(
                conditionDescription = "note list item removed ($title)",
                timeoutMillis = 20_000
            ) {
                composeUiTest
                    .onAllNodesWithContentDescription(title)
                    .fetchSemanticsNodes()
                    .isEmpty()
            }
            composeUiTest.awaitIdle()
        }
    }

    private suspend fun disableEncryption(password: String) {
        logger.i { "SettingsTestScreen: Disabling encryption" }
        openSettingsScreen()
        openSecurityCategory()
        settingsTestScreen {
            composeUiTest.waitUntilDisplayed("encryptionSwitch", blockSNI = ::encryptionSwitchSNI)
            encryptionSwitchSNI.performClick()
        }
        enterPasswordDialog {
            logger.i { "EnterPasswordDialog: Confirm disable encryption" }
            composeUiTest.waitUntilDisplayed("enterPasswordDialog", blockSNI = ::enterPasswordSNI)
            enterPasswordSNI.performTextReplacement(text = password)
            closeSoftKeyboard()
            confirmDialogButtonSNI.performSemanticsAction(SemanticsActions.OnClick)
        }
        composeUiTest.waitUntilNotExist(tag = ENTER_PASSWORD_DIALOG_TAG)
        backToMainScreen()
        logger.i { "SettingsTestScreen: Encryption disabled" }
    }

    private suspend fun importDatabase(password: String, incorrectPasswordTitle: String) {
        logger.i { "SettingsTestScreen: Import database" }
        openSettingsScreen()
        openBackupCategory()
        settingsTestScreen {
            composeUiTest.waitUntilDisplayed("importDatabase", blockSNI = ::importDatabaseSNI)
            importDatabaseSNI.performClick()
        }
        composeUiTest.awaitIdle()
        signIn(password, incorrectPasswordTitle)
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
        }
    }

    private suspend fun signIn(password: String, incorrectPasswordTitle: String) {
        signInScreen {
            logger.i { "SignInScreen: Attempting with incorrect password" }
            composeUiTest.waitUntilDisplayed("signInButton", blockSNI = ::signInButtonSNI)
            passwordFieldSNI.performTextReplacement(text = "wrong")
            closeSoftKeyboard()
            signInButtonSNI.performClick()
            composeUiTest.waitAssert("incorrect password label") {
                passwordLabelSNI.assertTextEquals(incorrectPasswordTitle)
            }
            logger.i { "SignInScreen: Entering correct password" }
            passwordFieldSNI.performTextReplacement(text = password)
            closeSoftKeyboard()
            signInButtonSNI.performClick()
        }
    }

    private suspend fun assertNotesVisible(titles: List<String>) {
        for (title in titles) {
            logger.i { "MainTestScreen: Verifying note list item $title" }
            MainTestScreen.noteItemTitleText = title
            mainTestScreen {
                composeUiTest.waitUntilDisplayed("noteListItem:$title", blockSNI = ::noteListItemSNI)
            }
        }
    }

    private suspend fun openSettingsScreen() {
        mainTestScreen {
            logger.i { "MainTestScreen: Opening settings screen" }
            composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
        }
    }

    private suspend fun openSecurityCategory() {
        settingsTestScreen {
            logger.i { "SettingsTestScreen: Opening Security category" }
            composeUiTest.waitUntilDisplayed("securityCategory", blockSNI = ::securityCategorySNI)
            securityCategorySNI.performClick()
        }
    }

    private suspend fun openBackupCategory() {
        settingsTestScreen {
            logger.i { "SettingsTestScreen: Opening Backup category" }
            composeUiTest.waitUntilDisplayed("backupCategory", blockSNI = ::backupCategorySNI)
            backupCategorySNI.performClick()
        }
    }

    private suspend fun backToMainScreen() {
        pressBack()
        composeUiTest.awaitIdle()
        pressBack()
        composeUiTest.awaitIdle()
    }
}
