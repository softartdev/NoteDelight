package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsPasswordTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val countingIdlingResource =
        com.softartdev.notedelight.shared.base.IdlingResource.countingIdlingResource

    private val composeIdlingResource = object : IdlingResource {
        override val isIdleNow: Boolean
            get() = countingIdlingResource.isIdleNow
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(countingIdlingResource)
        composeTestRule.registerIdlingResource(composeIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
        composeTestRule.unregisterIdlingResource(composeIdlingResource)
    }

    @Test
    fun settingPasswordTest() {
        composeTestRule.onNodeWithContentDescription(label = MR.strings.settings.contextLocalized())
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()
            .performClick()

        val enterPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_password))
            .assertIsDisplayed()
            .performClick()

        val visibilities: SemanticsNodeInteractionCollection = composeTestRule
            .onAllNodesWithContentDescription(label = Icons.Default.Visibility.name)
            .assertCountEquals(2)
        for (i in 0..1) {
            val visibility: SemanticsNodeInteraction = visibilities[i]
            visibility.performClick()
        }
        val confirmPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.confirm_password))
            .assertIsDisplayed()

        val yesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()
        yesSNI.performClick()

        composeTestRule.onNodeWithText(text = context.getString(R.string.empty_password))
            .assertIsDisplayed()

        enterPasswordSNI.performTextReplacement(text = "1")
        Espresso.closeSoftKeyboard()
        yesSNI.performClick()

        runBlocking { composeTestRule.awaitIdle() }
        composeTestRule.onAllNodes(isRoot()).printToLog("🦄", maxDepth = Int.MAX_VALUE)

        composeTestRule.onNodeWithText(text = context.getString(R.string.passwords_do_not_match))
            .assertIsDisplayed()
            .performClick()

        confirmPasswordSNI.performTextReplacement(text = "2")
        yesSNI.performClick()

        composeTestRule.onNodeWithText(text = context.getString(R.string.passwords_do_not_match))
            .assertIsDisplayed()

        confirmPasswordSNI.performTextReplacement(text = "1")
        yesSNI.performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        //TODO
    }
}