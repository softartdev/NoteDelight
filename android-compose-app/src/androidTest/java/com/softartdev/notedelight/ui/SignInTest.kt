@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.ComposeIdlingRes
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.shared.base.IdlingRes
import com.softartdev.notedelight.ui.cases.SignInTestCase
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class SignInTest {

    private val composeTestRule = customAndroidComposeRule<MainActivity>(
        beforeActivityLaunched = DbTestEncryptor::invoke
    )

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    private val composeUiTest: ComposeUiTest by lazy {
        val field = AndroidComposeTestRule::class.java.getDeclaredField("composeTest")
        field.isAccessible = true
        return@lazy field.get(composeTestRule) as AndroidComposeUiTest<*>
    }

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
    fun signInTest() = SignInTestCase(composeUiTest, Espresso::closeSoftKeyboard).invoke()
}