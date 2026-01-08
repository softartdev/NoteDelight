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
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection.Companion.Down
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.settings.security.change.ChangeAction
import com.softartdev.notedelight.presentation.settings.security.change.ChangeResult
import com.softartdev.notedelight.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.PasswordSaveButton
import com.softartdev.notedelight.ui.dialog.PreviewDialog
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_NEW_FIELD_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_NEW_LABEL_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_NEW_VISIBILITY_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_OLD_FIELD_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_OLD_LABEL_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_OLD_VISIBILITY_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_REPEAT_FIELD_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_REPEAT_LABEL_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_REPEAT_VISIBILITY_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_SAVE_BUTTON_TAG
import com.softartdev.notedelight.util.CHANGE_PASSWORD_DIALOG_TAG
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.cancel
import notedelight.ui.shared.generated.resources.changing_password_dialog_subtitle
import notedelight.ui.shared.generated.resources.changing_password_dialog_title
import notedelight.ui.shared.generated.resources.enter_new_password
import notedelight.ui.shared.generated.resources.enter_old_password
import notedelight.ui.shared.generated.resources.repeat_new_password
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChangePasswordDialog(changeViewModel: ChangeViewModel) {
    val result: ChangeResult by changeViewModel.stateFlow.collectAsState()
    val autofillManager: AutofillManager? = LocalAutofillManager.current
    LaunchedEffect(key1 = changeViewModel, key2 = autofillManager) {
        changeViewModel.autofillManager = autofillManager
    }
    ShowChangePasswordDialog(result, changeViewModel::onAction)
}

@Composable
fun ShowChangePasswordDialog(
    result: ChangeResult,
    onAction: (action: ChangeAction) -> Unit = {}
) = AlertDialog(
    modifier = Modifier.testTag(CHANGE_PASSWORD_DIALOG_TAG),
    title = { Text(text = stringResource(Res.string.changing_password_dialog_title)) },
    text = {
        val focusManager = LocalFocusManager.current
        Column {
            Text(text = stringResource(Res.string.changing_password_dialog_subtitle))
            Spacer(modifier = Modifier.height(8.dp))
            if (result.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            PasswordField(
                password = result.oldPassword,
                onPasswordChange = { onAction(ChangeAction.OnEditOldPassword(it)) },
                label = result.oldPasswordFieldLabel.resString,
                isError = result.isOldPasswordError,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(Down) }),
                contentDescription = stringResource(Res.string.enter_old_password),
                labelTag = CHANGE_PASSWORD_DIALOG_OLD_LABEL_TAG,
                visibilityTag = CHANGE_PASSWORD_DIALOG_OLD_VISIBILITY_TAG,
                fieldTag = CHANGE_PASSWORD_DIALOG_OLD_FIELD_TAG,
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                password = result.newPassword,
                onPasswordChange = { onAction(ChangeAction.OnEditNewPassword(it)) },
                label = result.newPasswordFieldLabel.resString,
                isError = result.isNewPasswordError,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(Down) }),
                contentDescription = stringResource(Res.string.enter_new_password),
                passwordContentType = ContentType.NewPassword,
                labelTag = CHANGE_PASSWORD_DIALOG_NEW_LABEL_TAG,
                visibilityTag = CHANGE_PASSWORD_DIALOG_NEW_VISIBILITY_TAG,
                fieldTag = CHANGE_PASSWORD_DIALOG_NEW_FIELD_TAG,
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                password = result.repeatNewPassword,
                onPasswordChange = { onAction(ChangeAction.OnEditRepeatPassword(it)) },
                label = result.repeatPasswordFieldLabel.resString,
                isError = result.isRepeatPasswordError,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { onAction(ChangeAction.OnChangeClick) }),
                contentDescription = stringResource(Res.string.repeat_new_password),
                passwordContentType = ContentType.NewPassword,
                labelTag = CHANGE_PASSWORD_DIALOG_REPEAT_LABEL_TAG,
                visibilityTag = CHANGE_PASSWORD_DIALOG_REPEAT_VISIBILITY_TAG,
                fieldTag = CHANGE_PASSWORD_DIALOG_REPEAT_FIELD_TAG,
            )
        }
    },
    confirmButton = { PasswordSaveButton(tag = CHANGE_PASSWORD_DIALOG_SAVE_BUTTON_TAG, onClick = { onAction(ChangeAction.OnChangeClick) }) },
    dismissButton = { Button(onClick = { onAction(ChangeAction.Cancel) }) { Text(stringResource(Res.string.cancel)) } },
    onDismissRequest = { onAction(ChangeAction.Cancel) }
)

@Preview
@Composable
fun PreviewChangePasswordDialog() = PreviewDialog {
    ShowChangePasswordDialog(ChangeResult())
}
