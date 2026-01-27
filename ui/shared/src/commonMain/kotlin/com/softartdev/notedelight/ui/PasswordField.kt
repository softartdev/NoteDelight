package com.softartdev.notedelight.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.enter_password
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    passwordState: MutableState<String> = mutableStateOf("password"),
    label: String = stringResource(Res.string.enter_password),
    isError: Boolean = true,
    imeAction: ImeAction = ImeAction.Unspecified,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentDescription: String = stringResource(Res.string.enter_password),
    passwordContentType: ContentType = ContentType.Password,
    labelTag: String,
    visibilityTag: String,
    fieldTag: String,
) = PasswordField(
    modifier = modifier,
    password = passwordState.value,
    onPasswordChange = passwordState::value::set,
    label = label,
    isError = isError,
    imeAction = imeAction,
    keyboardActions = keyboardActions,
    contentDescription = contentDescription,
    passwordContentType = passwordContentType,
    labelTag = labelTag,
    visibilityTag = visibilityTag,
    fieldTag = fieldTag,
)

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    password: String = "password",
    onPasswordChange: (String) -> Unit = {},
    label: String = stringResource(Res.string.enter_password),
    isError: Boolean = true,
    imeAction: ImeAction = ImeAction.Unspecified,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentDescription: String = stringResource(Res.string.enter_password),
    passwordContentType: ContentType = ContentType.Password,
    labelTag: String,
    visibilityTag: String,
    fieldTag: String,
) {
    val labelState by remember(label, isError) { mutableStateOf(label) } // workaround for ui-tests
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    TextField(
        modifier = modifier.testTag(fieldTag).semantics {
            contentType = passwordContentType
            this@semantics.contentDescription = contentDescription
        },
        label = { Text(labelState, modifier = Modifier.testTag(labelTag)) },
        leadingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility },
                modifier = Modifier.testTag(visibilityTag)) {
                Icon(
                    imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Password visibility"
                )
            }
        },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        value = password,
        onValueChange = onPasswordChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = keyboardActions,
        isError = isError,
        trailingIcon = { if (isError) Icon(Icons.Default.Error, Icons.Default.Error.name) else Unit },
        singleLine = true,
    )
}

@Preview
@Composable
fun PreviewPasswordField() = PasswordField(
    labelTag = "PREVIEW_PASSWORD_FIELD_LABEL_TAG",
    visibilityTag = "PREVIEW_PASSWORD_FIELD_VISIBILITY_TAG",
    fieldTag = "PREVIEW_PASSWORD_FIELD_FIELD_TAG",
)
