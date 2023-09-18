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

class EditTitleAfterCreateTestCase(
    composeTestRule: ComposeContentTestRule
) : () -> Unit, BaseTestCase(composeTestRule) {

    private val actualNoteTitle = "title"

    override fun invoke() {
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::fabSNI)
            fabSNI.performClick()
            noteScreen {
                val actualNoteText = UUID.randomUUID().toString().substring(0, 30)
                textFieldSNI.performTextInput(actualNoteText)
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
