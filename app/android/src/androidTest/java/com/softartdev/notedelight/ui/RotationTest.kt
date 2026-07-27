package com.softartdev.notedelight.ui

import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.util.CREATE_NOTE_FAB_TAG
import kotlinx.coroutines.test.runTest
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RotationTest {
    private val logger = Logger.withTag("RotationTest")
    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(composeTestRule)
        .around(ScreenOrientationRule(ScreenOrientation.PORTRAIT))
        .around(DetectLeaksAfterTestSuccess())

    @Before
    fun grantLocalNetworkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val permission = "android.permission.ACCESS_LOCAL_NETWORK"
            sequenceOf(
                instrumentation.targetContext.packageName,
                instrumentation.context.packageName
            ).forEach { packageName: String ->
                logger.d { "grant to $packageName the $permission" }
                instrumentation.uiAutomation.grantRuntimePermission(packageName, permission)
            }
        }
    }

    @Ignore("Unable to connect to Emulator gRPC port on CI")
    @Test
    fun rotationTest() = runTest {
        composeTestRule.awaitIdle()

        composeTestRule
            .onNodeWithTag(CREATE_NOTE_FAB_TAG)
            .assertIsDisplayed()

        onDevice().setScreenOrientation(ScreenOrientation.LANDSCAPE)
        composeTestRule.awaitIdle()

        composeTestRule
            .onNodeWithTag(CREATE_NOTE_FAB_TAG)
            .assertIsDisplayed()
    }
}
