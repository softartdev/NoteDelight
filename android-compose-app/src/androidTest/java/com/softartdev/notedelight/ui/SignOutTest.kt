package com.softartdev.notedelight.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.softartdev.notedelight.ComposeIdlingRes
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.shared.base.IdlingRes
import kotlinx.coroutines.test.runTest
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import notedelight.shared_compose_ui.generated.resources.Res
import notedelight.shared_compose_ui.generated.resources.create_note
import org.jetbrains.compose.resources.getString
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SignOutTest {

    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(IdlingRes.countingIdlingResource)
        composeTestRule.registerIdlingResource(ComposeIdlingRes)
    }

    @After
    fun unregisterIdlingResource() {
        composeTestRule.unregisterIdlingResource(ComposeIdlingRes)
        IdlingRegistry.getInstance().unregister(IdlingRes.countingIdlingResource)
    }

    @Test
    fun signOutTest() = runTest {
        composeTestRule
            .onNodeWithContentDescription(label = getString(Res.string.create_note))
            .assertIsDisplayed()

        Espresso.pressBackUnconditionally()

        assertTrue(composeTestRule.activityRule.scenario.state.isAtLeast(DESTROYED))
    }
}