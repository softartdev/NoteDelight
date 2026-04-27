@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.signin

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.softartdev.notedelight.presentation.signin.SignInAction
import com.softartdev.notedelight.util.SIGN_IN_BIOMETRIC_BUTTON_TAG
import com.softartdev.notedelight.util.SIGN_IN_BUTTON_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_FIELD_TAG
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignInScreenBiometricTest {

    @Test
    fun biometricButtonVisibilityDependsOnAvailability() = runComposeUiTest {
        setContent {
            SignInScreenBody(showLoading = false, showBiometricButton = true)
        }
        onNodeWithTag(SIGN_IN_BIOMETRIC_BUTTON_TAG).assertIsDisplayed()

        setContent {
            SignInScreenBody(showLoading = false, showBiometricButton = false)
        }
        onNodeWithTag(SIGN_IN_BIOMETRIC_BUTTON_TAG).assertDoesNotExist()
    }

    @Test
    fun fallbackPathPreservesPasswordEntryUx() = runComposeUiTest {
        val passwordState = mutableStateOf("")
        val actions = mutableListOf<SignInAction>()
        setContent {
            SignInScreenBody(
                showLoading = false,
                showBiometricButton = true,
                passwordState = passwordState,
                onAction = { action -> actions += action }
            )
        }

        onNodeWithTag(SIGN_IN_PASSWORD_FIELD_TAG, useUnmergedTree = true).performTextInput("pass123")
        onNodeWithTag(SIGN_IN_BIOMETRIC_BUTTON_TAG).performClick()
        assertEquals("pass123", passwordState.value)

        onNodeWithTag(SIGN_IN_BUTTON_TAG).performClick()
        assertTrue(actions.contains(SignInAction.OnBiometricSignInClick))
        assertTrue(actions.contains(SignInAction.OnSignInClick("pass123")))
    }
}
