@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.ui.screen.MainTestScreen.Companion.noteItemTitleText
import com.softartdev.notedelight.util.UUID
import com.softartdev.notedelight.waitUntilDisplayed

class CrudTestCase(
    composeTestRule: ComposeUiTest
) : () -> Unit, BaseTestCase(composeTestRule) {

    private val actualNoteText = UUID.randomUUID().toString().substring(0, 30)

    override fun invoke() {
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
                deleteNoteMenuButtonSNI.performClick()
                commonDialog {
                    yesDialogButtonSNI.performClick()
                }
            }
            composeTestRule.waitUntilDisplayed(blockSNI = ::emptyResultLabelSNI)
        }
    }
}