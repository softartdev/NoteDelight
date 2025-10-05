package com.softartdev.notedelight.ui

import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.ui.cases.SignInTestCase
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
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

    @Test
    fun signInTest() = SignInTestCase(composeTestRule, Espresso::closeSoftKeyboard).invoke()
}