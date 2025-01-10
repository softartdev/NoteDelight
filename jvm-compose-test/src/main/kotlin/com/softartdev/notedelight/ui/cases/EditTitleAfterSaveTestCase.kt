package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen.Companion.noteItemTitleText
import com.softartdev.notedelight.waitUntilDisplayed
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.enter_title
import org.jetbrains.compose.resources.getString
import java.util.UUID

class EditTitleAfterSaveTestCase(
    composeTestRule: ComposeContentTestRule
) : () -> Unit, BaseTestCase(composeTestRule) {

    private val actualNoteText = UUID.randomUUID().toString().substring(0, 30)
    private val actualNoteTitle = "title"

    override fun invoke() = runTest {
        mainTestScreen {
            composeTestRule.waitUntilDisplayed(blockSNI = ::fabSNI)
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
                    .onNodeWithContentDescription(label = runBlocking { getString(Res.string.enter_title) })
                    .assertDoesNotExist()
                saveNoteMenuButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            noteItemTitleText = actualNoteTitle
            composeTestRule.waitUntilDisplayed(blockSNI = ::noteListItemSNI)
        }
    }
}
