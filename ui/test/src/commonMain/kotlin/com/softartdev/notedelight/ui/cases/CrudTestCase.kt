@file:OptIn(ExperimentalTestApi::class, ExperimentalUuidApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen.Companion.noteItemTitleText
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CrudTestCase(
    composeUiTest: ComposeUiTest
) : () -> TestResult, BaseTestCase(composeUiTest) {

    private val actualNoteText = Uuid.random().toString().substring(0, 30)

    override fun invoke() = runTest {
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("fab", blockSNI = ::fabSNI)
            fabSNI.performClick()
            noteScreen {
                textFieldSNI.performTextInput(actualNoteText)
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            noteItemTitleText = actualNoteText
            composeUiTest.waitUntilDisplayed("noteListItem", blockSNI = ::noteListItemSNI)
            noteListItemSNI.performClick()
            noteScreen {
                deleteNoteMenuButtonSNI.performClick()
                commonDialog {
                    confirmDialogButtonSNI.performClick()
                }
            }
            composeUiTest.waitUntilDisplayed("emptyResultLabel", blockSNI = ::emptyResultLabelSNI)
        }
    }
}