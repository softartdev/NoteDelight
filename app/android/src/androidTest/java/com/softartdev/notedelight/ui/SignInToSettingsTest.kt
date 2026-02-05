@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.reflect
import com.softartdev.notedelight.ui.cases.SignInToSettingsTestCase
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class SignInToSettingsTest {

    private val composeTestRule = customAndroidComposeRule<MainActivity>(
        beforeActivityLaunched = DbTestEncryptor::invoke
    )

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    private val composeUiTest: ComposeUiTest = reflect(composeTestRule)

    @Test
    fun signInToSettingsTest() = SignInToSettingsTestCase(
        composeUiTest,
        Espresso::closeSoftKeyboard,
        Espresso::pressBack,
    ).invoke()
}
