@file:OptIn(ExperimentalTestApi::class, ExperimentalUuidApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.uuid.ExperimentalUuidApi

class CreateNoteWhileSelectedTestCase(
    composeUiTest: ComposeUiTest
) : () -> TestResult, BaseTestCase(composeUiTest) {

    override fun invoke() = runTest {
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("fab", blockSNI = ::fabSNI)
            fabSNI.performClick()
        }
        noteScreen {
            composeUiTest.waitUntilDisplayed("noteTextField", blockSNI = ::textFieldSNI)
            textFieldSNI.performTextInput("1")
            saveNoteMenuButtonSNI.performClick()
            textFieldSNI.performTextInput("2")
            saveNoteMenuButtonSNI.performClick()
            textFieldSNI.performTextInput("3")
        }
        mainTestScreen {
            val fabVisible = runCatching(this@mainTestScreen::fabSNI).isSuccess // `assertIsDisplayed` invokes in getter
            if (!fabVisible) return@runTest // fab must be visible in tablet, but not in phone layout
            fabSNI.performClick()
        }
        saveDialog {
            composeUiTest.waitUntilDisplayed("dialogSNI", blockSNI = ::dialogSNI)
            confirmDialogButtonSNI.performClick()
        }
        MainTestScreen.noteItemTitleText = "1"
        mainTestScreen {
            noteListItemSNI.performClick()
        }
        noteScreen {
            textFieldSNI.assertTextContains("123")
        }
    }
}
