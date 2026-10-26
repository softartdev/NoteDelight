@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import android.content.ClipDescription
import android.content.Intent
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.DbTestEncryptor
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.reflect
import com.softartdev.notedelight.ui.cases.ShareTestCase
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class ShareIntentTest {

    private val composeTestRule = customAndroidComposeRule<MainActivity>(
        beforeActivityLaunched = {
            if (TestDescriptionHolder.testDescription.methodName == ::shareEncryptedDbTest.name) {
                DbTestEncryptor()
            }
        }
    )

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(composeTestRule)
        .around(DetectLeaksAfterTestSuccess())

    private val composeUiTest: ComposeUiTest = reflect(composeTestRule)

    @Test
    fun shareUnencryptedDbTest() = ShareTestCase(
        composeUiTest = composeUiTest,
        closeSoftKeyboard = Espresso::closeSoftKeyboard,
        sendText = ::sendShareIntent,
        isDbEncrypted = false,
    ).invoke()

    @Test
    fun shareEncryptedDbTest() = ShareTestCase(
        composeUiTest = composeUiTest,
        closeSoftKeyboard = Espresso::closeSoftKeyboard,
        sendText = ::sendShareIntent,
        isDbEncrypted = true,
    ).invoke()

    private fun sendShareIntent(text: String) {
        composeTestRule.activityRule.scenario.onActivity { activity ->
            val sendIntent = Intent(Intent.ACTION_SEND)
                .setType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                .putExtra(Intent.EXTRA_TEXT, text)
            activity.onNewIntent(sendIntent)
        }
    }
}
