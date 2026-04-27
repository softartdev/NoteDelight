package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.settings.security.biometric.BiometricEnrollAction
import com.softartdev.notedelight.presentation.settings.security.biometric.BiometricEnrollResult
import com.softartdev.notedelight.presentation.settings.security.biometric.BiometricEnrollViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.PasswordSaveButton
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_FIELD_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_LABEL_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_SAVE_BUTTON_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_TAG
import com.softartdev.notedelight.util.BIOMETRIC_ENROLL_DIALOG_VISIBILITY_TAG
import notedelight.core.ui.generated.resources.Res
import notedelight.core.ui.generated.resources.biometric_enroll_dialog_subtitle
import notedelight.core.ui.generated.resources.biometric_enroll_dialog_title
import notedelight.core.ui.generated.resources.biometric_prompt_negative_button
import notedelight.core.ui.generated.resources.biometric_prompt_subtitle
import notedelight.core.ui.generated.resources.biometric_prompt_title
import notedelight.core.ui.generated.resources.cancel
import notedelight.core.ui.generated.resources.enter_password
import org.jetbrains.compose.resources.stringResource

@Composable
fun BiometricEnrollDialog(biometricEnrollViewModel: BiometricEnrollViewModel) {
    val result: BiometricEnrollResult by biometricEnrollViewModel.stateFlow.collectAsState()
    val title = stringResource(Res.string.biometric_prompt_title)
    val subtitle = stringResource(Res.string.biometric_prompt_subtitle)
    val negative = stringResource(Res.string.biometric_prompt_negative_button)
    ShowBiometricEnrollDialog(result) { action ->
        val resolved = if (action is BiometricEnrollAction.OnEnrollClick) {
            BiometricEnrollAction.OnEnrollClick(title, subtitle, negative)
        } else action
        biometricEnrollViewModel.onAction(resolved)
    }
}

@Composable
fun ShowBiometricEnrollDialog(
    result: BiometricEnrollResult,
    onAction: (action: BiometricEnrollAction) -> Unit = {},
) = AlertDialog(
    modifier = Modifier.testTag(BIOMETRIC_ENROLL_DIALOG_TAG),
    title = { Text(text = stringResource(Res.string.biometric_enroll_dialog_title)) },
    text = {
        Column {
            Text(text = stringResource(Res.string.biometric_enroll_dialog_subtitle))
            Spacer(modifier = Modifier.height(8.dp))
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            PasswordField(
                modifier = Modifier.fillMaxWidth(),
                password = result.password,
                onPasswordChange = { onAction(BiometricEnrollAction.OnEditPassword(it)) },
                label = result.fieldLabel.resString,
                isError = result.isError,
                contentDescription = stringResource(Res.string.enter_password),
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions {
                    onAction(BiometricEnrollAction.OnEnrollClick("", "", ""))
                },
                labelTag = BIOMETRIC_ENROLL_DIALOG_LABEL_TAG,
                visibilityTag = BIOMETRIC_ENROLL_DIALOG_VISIBILITY_TAG,
                fieldTag = BIOMETRIC_ENROLL_DIALOG_FIELD_TAG,
            )
        }
    },
    confirmButton = {
        PasswordSaveButton(
            tag = BIOMETRIC_ENROLL_DIALOG_SAVE_BUTTON_TAG,
            onClick = { onAction(BiometricEnrollAction.OnEnrollClick("", "", "")) },
        )
    },
    dismissButton = {
        Button(onClick = { onAction(BiometricEnrollAction.Cancel) }) {
            Text(stringResource(Res.string.cancel))
        }
    },
    onDismissRequest = { onAction(BiometricEnrollAction.Cancel) },
)

@Preview
@Composable
fun PreviewBiometricEnrollDialog() = PreviewDialog {
    ShowBiometricEnrollDialog(BiometricEnrollResult(loading = true))
}
