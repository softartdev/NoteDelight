package com.softartdev.notedelight

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import com.softartdev.notedelight.shared.base.IdlingResource
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.SkipLeakDetection
import leakcanary.TestDescriptionHolder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import java.util.*

class EditTitleTest {

    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(IdlingResource.countingIdlingResource)
        composeTestRule.registerIdlingResource(composeIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        composeTestRule.unregisterIdlingResource(composeIdlingResource)
        IdlingRegistry.getInstance().unregister(IdlingResource.countingIdlingResource)
    }

    //TODO remove skip after update Jetpack Compose version above 1.1.0
    @SkipLeakDetection("See https://issuetracker.google.com/issues/202190483")
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