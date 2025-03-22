package com.softartdev.notedelight.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.softartdev.notedelight.MainActivity
import kotlinx.coroutines.test.runTest
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.create_note
import org.jetbrains.compose.resources.getString
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RotationTest {

    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(ScreenOrientationRule(ScreenOrientation.PORTRAIT))
        .around(composeTestRule)

    @Ignore("Unable to connect to Emulator gRPC port on CI")
    @Test
    fun rotationTest() = runTest {
        composeTestRule
            .onNodeWithContentDescription(label = getString(Res.string.create_note))
            .assertIsDisplayed()

        onDevice().setScreenOrientation(ScreenOrientation.LANDSCAPE)

        composeTestRule
            .onNodeWithContentDescription(label = getString(Res.string.create_note))
            .assertIsDisplayed()

        onDevice().setScreenOrientation(ScreenOrientation.PORTRAIT)
    }
}