package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.Test
import java.util.*

class EditTitleTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val detectLeaksRule = DetectLeaksAfterTestSuccess()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun editTitleTest() {
        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.create_note))
            .assertIsDisplayed()
            .performClick()

        val actualNoteText = UUID.randomUUID().toString().substring(0, 30)
        composeTestRule.onNodeWithText(text = context.getString(R.string.type_text))
            .assertIsDisplayed()
            .performTextInput(actualNoteText)

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.action_save_note))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = Icons.Default.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = actualNoteText)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.action_edit_title))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onAllNodes(isRoot()).printToLog("🦄", maxDepth = Int.MAX_VALUE)

        val actualNoteTitle = "title"
        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.enter_title))
            .assertIsDisplayed()
            .performTextReplacement(actualNoteTitle)

        composeTestRule.onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.action_save_note))
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = Icons.Default.ArrowBack.name)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = actualNoteTitle)
            .assertIsDisplayed()
    }
}