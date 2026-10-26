@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

class ShareTestCase(
    composeUiTest: ComposeUiTest,
    private val closeSoftKeyboard: () -> Unit,
    private val sendText: (String) -> Unit,
    private val isDbEncrypted: Boolean = false,
) : () -> TestResult, BaseTestCase(composeUiTest) {

    private val sharedText = "Shared secret note"

    override fun invoke() = runTest {
        if (isDbEncrypted) {
            signInScreen {
                composeUiTest.waitUntilDisplayed("passwordField", blockSNI = ::passwordFieldSNI)
                sendText(sharedText)
                passwordFieldSNI.performTextReplacement(text = DbTestEncryptor.PASSWORD)
                closeSoftKeyboard()
                signInButtonSNI.performClick()
            }
        } else {
            mainTestScreen {
                composeUiTest.waitUntilDisplayed("emptyResultLabel", blockSNI = ::emptyResultLabelSNI)
                sendText(sharedText)
            }
        }
        noteScreen {
            composeUiTest.waitUntilDisplayed("noteTextField", blockSNI = ::textFieldSNI)
            textFieldSNI.assertTextContains(sharedText)
            shareNoteMenuButtonSNI.assertIsDisplayed()
            backButtonSNI.performClick()
        }
        MainTestScreen.noteItemTitleText = sharedText
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("noteListItem", blockSNI = ::noteListItemSNI)
        }
    }
}
