package com.softartdev.notedelight.compose

import android.content.Context
import androidx.compose.ui.test.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.shared.test.util.Encryptor
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class SignInTest {

    @get:Rule
    val composeTestRule = customAndroidComposeRule<MainActivity>(
        beforeActivityLaunched = Encryptor::encryptDB
    )

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
    fun signInTest() {
        val passwordFieldSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.enter_password))
            .assertIsDisplayed()

        val signInButtonSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.sign_in))
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        passwordFieldSNI.assertTextEquals(context.getString(R.string.empty_password), includeEditableText = false)
    }
}