@file:OptIn(ExperimentalTestApi::class)

package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.softartdev.notedelight.ui.screen.dialog.BiometricDisableConfirmationDialog as BiometricDisableConfirmationDialogScreen
import com.softartdev.notedelight.ui.screen.dialog.CommonDialogImpl
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BiometricDisableConfirmationDialogTest {

    @Test
    fun confirmClickCallsConfirm() = runComposeUiTest {
        var confirmed = false
        setContent {
            BiometricDisableConfirmationDialog(
                onConfirm = { confirmed = true },
            )
        }

        val dialog = BiometricDisableConfirmationDialogScreen(CommonDialogImpl(this))
        dialog.dialogSNI.assertIsDisplayed()
        dialog.confirmDialogButtonSNI.performClick()

        assertTrue(confirmed)
    }

    @Test
    fun cancelClickCallsDismiss() = runComposeUiTest {
        var dismissed = false
        setContent {
            BiometricDisableConfirmationDialog(
                onDismiss = { dismissed = true },
            )
        }

        val dialog = BiometricDisableConfirmationDialogScreen(CommonDialogImpl(this))
        dialog.dialogSNI.assertIsDisplayed()
        dialog.cancelDialogButtonSNI.performClick()

        assertTrue(dismissed)
    }

    @Test
    fun confirmClickDoesNotCallDismiss() = runComposeUiTest {
        var dismissed = false
        setContent {
            BiometricDisableConfirmationDialog(
                onDismiss = { dismissed = true },
            )
        }

        val dialog = BiometricDisableConfirmationDialogScreen(CommonDialogImpl(this))
        dialog.confirmDialogButtonSNI.performClick()

        assertFalse(dismissed)
    }
}
