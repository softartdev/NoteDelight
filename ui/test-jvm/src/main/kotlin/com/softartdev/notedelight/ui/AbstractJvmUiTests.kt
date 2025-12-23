@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.softartdev.notedelight.AbstractUITests
import com.softartdev.notedelight.reflect
import com.softartdev.notedelight.util.ComposeCountingIdlingResource

abstract class AbstractJvmUiTests : AbstractUITests() {
    abstract val composeTestRule: ComposeContentTestRule
    override val composeUiTest: ComposeUiTest by lazy { reflect(composeTestRule) }

    override fun setUp() {
        super.setUp()
        composeTestRule.registerIdlingResource(ComposeCountingIdlingResource)
    }

    override fun tearDown() {
        super.tearDown()
        composeTestRule.unregisterIdlingResource(ComposeCountingIdlingResource)
    }
}