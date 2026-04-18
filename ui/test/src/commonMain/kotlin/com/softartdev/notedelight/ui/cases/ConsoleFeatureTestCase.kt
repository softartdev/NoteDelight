@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.cases

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.feature.console.ui.CONSOLE_INPUT_FIELD_TAG
import com.softartdev.notedelight.feature.console.ui.CONSOLE_RUN_BUTTON_TAG
import com.softartdev.notedelight.feature.console.ui.CONSOLE_TRANSCRIPT_TAG
import com.softartdev.notedelight.ui.BaseTestCase
import com.softartdev.notedelight.util.CONSOLE_TIPS_BUTTON_TAG
import com.softartdev.notedelight.util.CONSOLE_TIP_AUTOFILL_PREFIX
import com.softartdev.notedelight.waitUntilDisplayed
import com.softartdev.notedelight.waitAssert
import com.softartdev.notedelight.ASSERT_WAIT_TIMEOUT_MILLIS
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.minutes

class ConsoleFeatureTestCase(
    composeUiTest: ComposeUiTest,
    private val pressBack: () -> Unit,
) : () -> TestResult, BaseTestCase(composeUiTest) {
    private val logger = Logger.withTag("ConsoleFeatureTestCase")

    override fun invoke() = runTest(timeout = 5.minutes) {
        logger.i { "Starting ConsoleFeatureTestCase" }
        // Navigate to Settings
        mainTestScreen {
            composeUiTest.waitUntilDisplayed("settingsMenuButton", blockSNI = ::settingsMenuButtonSNI)
            settingsMenuButtonSNI.performClick()
        }
        // Select Console category
        settingsTestScreen {
            composeUiTest.waitUntilDisplayed("consoleCategory", blockSNI = ::consoleCategorySNI)
            consoleCategorySNI.performClick()
        }
        // Open tips menu and autofill SELECT last_insert_rowid();
        logger.i { "Opening tips menu" }
        composeUiTest.waitUntilDisplayed("tipsButton") {
            composeUiTest.onNodeWithTag(CONSOLE_TIPS_BUTTON_TAG).assertExists()
        }
        composeUiTest.onNodeWithTag(CONSOLE_TIPS_BUTTON_TAG).performClick()
        composeUiTest.awaitIdle()
        // Autofill the 3rd tip: SELECT last_insert_rowid();
        val autofillTag = "${CONSOLE_TIP_AUTOFILL_PREFIX}2"
        composeUiTest.waitUntil(
            conditionDescription = "autofill tip visible",
            timeoutMillis = 10_000
        ) {
            composeUiTest.onAllNodes(hasTestTag(autofillTag)).fetchSemanticsNodes().isNotEmpty()
        }
        composeUiTest.onNodeWithTag(autofillTag).performClick()
        composeUiTest.awaitIdle()
        // Verify input field has the autofilled command
        logger.i { "Verifying autofill" }
        composeUiTest.waitAssert("input has autofilled text") {
            composeUiTest.onNodeWithTag(CONSOLE_INPUT_FIELD_TAG)
                .assertTextContains("SELECT last_insert_rowid();")
        }
        // Tap Run
        logger.i { "Tapping Run" }
        composeUiTest.onNodeWithTag(CONSOLE_RUN_BUTTON_TAG).performClick()
        composeUiTest.awaitIdle()
        // Assert transcript contains the command, "0", and "Query returned 1 row(s)."
        logger.i { "Asserting transcript content" }
        composeUiTest.waitUntil(
            conditionDescription = "transcript visible",
            timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS,
        ) {
            composeUiTest.onAllNodes(hasTestTag(CONSOLE_TRANSCRIPT_TAG))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeUiTest.waitUntil(
            conditionDescription = "transcript contains command",
            timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS,
        ) {
            composeUiTest.onAllNodes(hasText("sqlite> SELECT last_insert_rowid();", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeUiTest.waitUntil(
            conditionDescription = "transcript contains 0",
            timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS,
        ) {
            composeUiTest.onAllNodes(hasText("0"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeUiTest.waitUntil(
            conditionDescription = "transcript contains row count",
            timeoutMillis = ASSERT_WAIT_TIMEOUT_MILLIS,
        ) {
            composeUiTest.onAllNodes(hasText("Query returned 1 row(s).", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        // Navigate back
        pressBack()
        composeUiTest.awaitIdle()
        pressBack()
        composeUiTest.awaitIdle()
        logger.i { "Ending ConsoleFeatureTestCase" }
    }
}
