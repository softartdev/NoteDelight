package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.presentation.settings.security.biometric.BiometricDisableViewModel
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.notedelight.util.BIOMETRIC_DISABLE_CONFIRMATION_DIALOG_TAG
import com.softartdev.notedelight.util.CANCEL_BUTTON_TAG
import com.softartdev.notedelight.util.YES_BUTTON_TAG
import notedelight.core.ui.generated.resources.Res
import notedelight.core.ui.generated.resources.biometric_disable_dialog_confirm
import notedelight.core.ui.generated.resources.biometric_disable_dialog_message
import notedelight.core.ui.generated.resources.biometric_disable_dialog_title
import notedelight.core.ui.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun BiometricDisableConfirmationDialog(
    biometricDisableViewModel: BiometricDisableViewModel,
) = BiometricDisableConfirmationDialog(
    onConfirm = biometricDisableViewModel::disableBiometricAndNavBack,
    onDismiss = biometricDisableViewModel::doNotDisableBiometricAndNavBack,
)

@Composable
fun BiometricDisableConfirmationDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) = AlertDialog(
    modifier = Modifier.testTag(BIOMETRIC_DISABLE_CONFIRMATION_DIALOG_TAG),
    title = { Text(stringResource(Res.string.biometric_disable_dialog_title)) },
    text = { Text(stringResource(Res.string.biometric_disable_dialog_message)) },
    confirmButton = {
        Button(
            modifier = Modifier.testTag(YES_BUTTON_TAG),
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) { Text(stringResource(Res.string.biometric_disable_dialog_confirm)) }
    },
    dismissButton = {
        TextButton(
            modifier = Modifier.testTag(CANCEL_BUTTON_TAG),
            onClick = onDismiss,
        ) { Text(stringResource(Res.string.cancel)) }
    },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewBiometricDisableConfirmationDialog() = PreviewDialog {
    BiometricDisableConfirmationDialog()
}
