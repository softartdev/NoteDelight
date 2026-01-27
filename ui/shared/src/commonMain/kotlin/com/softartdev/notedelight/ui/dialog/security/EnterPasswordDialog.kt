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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillManager
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.settings.security.enter.EnterAction
import com.softartdev.notedelight.presentation.settings.security.enter.EnterResult
import com.softartdev.notedelight.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.PasswordSaveButton
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.notedelight.util.ENTER_PASSWORD_DIALOG_FIELD_TAG
import com.softartdev.notedelight.util.ENTER_PASSWORD_DIALOG_LABEL_TAG
import com.softartdev.notedelight.util.ENTER_PASSWORD_DIALOG_SAVE_BUTTON_TAG
import com.softartdev.notedelight.util.ENTER_PASSWORD_DIALOG_TAG
import com.softartdev.notedelight.util.ENTER_PASSWORD_DIALOG_VISIBILITY_TAG
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.cancel
import notedelight.ui.shared.generated.resources.enter_password
import notedelight.ui.shared.generated.resources.enter_password_dialog_subtitle
import notedelight.ui.shared.generated.resources.enter_password_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EnterPasswordDialog(enterViewModel: EnterViewModel) {
    val result: EnterResult by enterViewModel.stateFlow.collectAsState()
    val autofillManager: AutofillManager? = LocalAutofillManager.current
    LaunchedEffect(key1 = enterViewModel, key2 = autofillManager) {
        enterViewModel.autofillManager = autofillManager
    }
    ShowEnterPasswordDialog(result, enterViewModel::onAction)
}

@Composable
fun ShowEnterPasswordDialog(
    result: EnterResult,
    onAction: (action: EnterAction) -> Unit = {}
) = AlertDialog(
    modifier = Modifier.testTag(ENTER_PASSWORD_DIALOG_TAG),
    title = { Text(text = stringResource(Res.string.enter_password_dialog_title)) },
    text = {
        Column {
            Text(text = stringResource(Res.string.enter_password_dialog_subtitle))
            Spacer(modifier = Modifier.height(8.dp))
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            PasswordField(
                modifier = Modifier.fillMaxWidth(),
                password = result.password,
                onPasswordChange = { onAction(EnterAction.OnEditPassword(it)) },
                label = result.fieldLabel.resString,
                isError = result.isError,
                contentDescription = stringResource(Res.string.enter_password),
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions { onAction(EnterAction.OnEnterClick) },
                labelTag = ENTER_PASSWORD_DIALOG_LABEL_TAG,
                visibilityTag = ENTER_PASSWORD_DIALOG_VISIBILITY_TAG,
                fieldTag = ENTER_PASSWORD_DIALOG_FIELD_TAG
            )
        }
    },
    confirmButton = { PasswordSaveButton(tag = ENTER_PASSWORD_DIALOG_SAVE_BUTTON_TAG, onClick = { onAction(EnterAction.OnEnterClick) }) },
    dismissButton = { Button(onClick = { onAction(EnterAction.Cancel) }) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = { onAction(EnterAction.Cancel) }
)

@Preview
@Composable
fun PreviewEnterPasswordDialog() = PreviewDialog { ShowEnterPasswordDialog(EnterResult(loading = true)) }