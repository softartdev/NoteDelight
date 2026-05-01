@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.notedelight.MainActivity
import com.softartdev.notedelight.di.biometricTestModule
import com.softartdev.notedelight.interactor.TestBiometricInteractor
import com.softartdev.notedelight.reflect
import com.softartdev.notedelight.ui.cases.BiometricSettingsTestCase
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.mp.KoinPlatformTools

@FlakyTest
@RunWith(AndroidJUnit4::class)
class BiometricSettingsTest {

    private val testBiometricInteractor: TestBiometricInteractor
        get() = KoinPlatformTools.defaultContext().get().get(TestBiometricInteractor::class)

    private val composeTestRule = customAndroidComposeRule<MainActivity>(
        beforeActivityLaunched = {
            loadKoinModules(biometricTestModule)
            testBiometricInteractor.reset(canAuthenticateResult = true)
        }
    )

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    private val composeUiTest: ComposeUiTest = reflect(composeTestRule)

    @After
    fun tearDown() = testBiometricInteractor.reset()

    @Test
    fun biometricSettingsTest() = BiometricSettingsTestCase(
        composeUiTest = composeUiTest,
        closeSoftKeyboard = Espresso::closeSoftKeyboard,
    ).invoke()
}
