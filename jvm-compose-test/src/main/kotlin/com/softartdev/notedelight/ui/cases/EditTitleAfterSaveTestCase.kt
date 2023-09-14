package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen.Companion.noteItemTitleText
import com.softartdev.notedelight.waitUntilDisplayed
import java.util.UUID

class EditTitleAfterSaveTestCase(
    composeTestRule: ComposeContentTestRule
) : () -> Unit, BaseTestCase(composeTestRule) {

    private val actualNoteText = UUID.randomUUID().toString().substring(0, 30)
    private val actualNoteTitle = "title"

    override fun invoke() {
        mainTestScreen {
            fabSNI.performClick()
            noteScreen {
                textFieldSNI.performTextInput(actualNoteText)
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            noteItemTitleText = actualNoteText
            composeTestRule.waitUntilDisplayed(blockSNI = ::noteListItemSNI)
            noteListItemSNI.performClick()
            noteScreen {
                editTitleMenuButtonSNI.performClick()
                editTitleDialog {
                    editTitleSNI.performTextReplacement(actualNoteTitle)
                    yesDialogButtonSNI.performClick()
                }
                composeTestRule
                    .onNodeWithContentDescription(label = MR.strings.enter_title.contextLocalized())
                    .assertDoesNotExist()
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            noteItemTitleText = actualNoteTitle
            composeTestRule.waitUntilDisplayed(blockSNI = ::noteListItemSNI)
        }
    }
}
