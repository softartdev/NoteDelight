@file:OptIn(ExperimentalTestApi::class, ExperimentalUuidApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen.Companion.noteItemTitleText
import com.softartdev.notedelight.util.runBlockingAll
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.enter_title
import org.jetbrains.compose.resources.getString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class EditTitleAfterSaveTestCase(
    composeUiTest: ComposeUiTest
) : () -> TestResult, BaseTestCase(composeUiTest) {

    private val actualNoteText = Uuid.random().toString().substring(0, 30)
    private val actualNoteTitle = "title"

    override fun invoke() = runTest {
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("fab", blockSNI = ::fabSNI)
            fabSNI.performClick()
            noteScreen {
                composeUiTest.waitUntilDisplayed("Note Text Field", blockSNI = ::textFieldSNI)
                textFieldSNI.performTextInput(actualNoteText)
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            noteItemTitleText = actualNoteText
            composeUiTest.waitUntilDisplayed("noteListItem", blockSNI = ::noteListItemSNI)
            noteListItemSNI.performClick()
            noteScreen {
                editTitleMenuButtonSNI.performClick()
                editTitleDialog {
                    editTitleSNI.performTextReplacement(actualNoteTitle)
                    confirmDialogButtonSNI.performClick()
                }
                composeUiTest
                    .onNodeWithContentDescription(label = runBlockingAll { getString(Res.string.enter_title) })
                    .assertDoesNotExist()
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            noteItemTitleText = actualNoteTitle
            composeUiTest.waitUntilDisplayed("noteListItem", blockSNI = ::noteListItemSNI)
        }
    }
}
