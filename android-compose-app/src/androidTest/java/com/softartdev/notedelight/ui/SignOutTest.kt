package com.softartdev.notedelight.ui

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.softartdev.notedelight.ComposeIdlingRes
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.shared.R
import com.softartdev.notedelight.shared.base.IdlingRes
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
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

    private val context = ApplicationProvider.getApplicationContext<Context>()

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
    fun signOutTest() {
        composeTestRule
            .onNodeWithContentDescription(label = context.getString(R.string.create_note))
            .assertIsDisplayed()

        Espresso.pressBackUnconditionally()

        assertTrue(composeTestRule.activityRule.scenario.state.isAtLeast(DESTROYED))
    }
}