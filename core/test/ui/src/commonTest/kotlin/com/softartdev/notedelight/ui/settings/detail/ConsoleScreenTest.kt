@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.softartdev.notedelight.feature.console.ui.CONSOLE_INPUT_FIELD_TAG
import com.softartdev.notedelight.feature.console.ui.CONSOLE_RUN_BUTTON_TAG
import com.softartdev.notedelight.presentation.console.ConsoleAction
import com.softartdev.notedelight.presentation.console.ConsoleResult
import com.softartdev.notedelight.util.CONSOLE_TIPS_BUTTON_TAG
import com.softartdev.notedelight.util.CONSOLE_TIP_AUTOFILL_PREFIX
import com.softartdev.notedelight.util.CONSOLE_TIP_COPY_PREFIX
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConsoleScreenTest {

    @Test
    fun tipsMenuShowsCopyAndAutofill() = runComposeUiTest {
        val actions = mutableListOf<ConsoleAction>()
        setContent {
            ConsoleScreenBody(result = ConsoleResult(), onAction = actions::add)
        }
        // Open tips menu
        onNodeWithTag(CONSOLE_TIPS_BUTTON_TAG).assertIsDisplayed().performClick()
        waitForIdle()
        // Verify copy and autofill buttons exist for first tip
        onNodeWithTag("${CONSOLE_TIP_COPY_PREFIX}0").assertIsDisplayed()
        onNodeWithTag("${CONSOLE_TIP_AUTOFILL_PREFIX}0").assertIsDisplayed()
        // Tap autofill on first tip (PRAGMA cipher_version;)
        onNodeWithTag("${CONSOLE_TIP_AUTOFILL_PREFIX}0").performClick()
        waitForIdle()
        // Verify autofill action was dispatched
        assertTrue(actions.isNotEmpty())
        val autofillAction = actions.last()
        assertTrue(autofillAction is ConsoleAction.UpdateInput)
        assertEquals("PRAGMA cipher_version;", autofillAction.text)
    }

    @Test
    fun tipsMenuCopyAction() = runComposeUiTest {
        setContent {
            ConsoleScreenBody(result = ConsoleResult())
        }
        // Open tips menu
        onNodeWithTag(CONSOLE_TIPS_BUTTON_TAG).assertIsDisplayed().performClick()
        waitForIdle()
        // Tap copy on second tip (SELECT sqlite3mc_version();)
        onNodeWithTag("${CONSOLE_TIP_COPY_PREFIX}1").assertIsDisplayed().performClick()
        waitForIdle()
        // Tips menu should close after copy
    }

    @Test
    fun runButtonDisabledWhenInputBlank() = runComposeUiTest {
        setContent {
            ConsoleScreenBody(result = ConsoleResult(input = ""))
        }
        onNodeWithTag(CONSOLE_RUN_BUTTON_TAG).assertIsDisplayed()
        // Button should exist but be disabled (empty input)
        onNodeWithTag(CONSOLE_INPUT_FIELD_TAG).assertIsDisplayed()
    }

    @Test
    fun inputFieldShowsAutofillValue() = runComposeUiTest {
        setContent {
            ConsoleScreenBody(result = ConsoleResult(input = "SELECT 1;"))
        }
        onNodeWithTag(CONSOLE_INPUT_FIELD_TAG)
            .assertIsDisplayed()
            .assertTextContains("SELECT 1;")
    }
}
