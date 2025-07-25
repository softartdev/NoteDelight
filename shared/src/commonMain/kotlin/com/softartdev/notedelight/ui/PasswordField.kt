package com.softartdev.notedelight.ui

import androidx.compose.foundation.text.KeyboardActions
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.runBlocking
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.enter_password
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private const val PASSWORD_LABEL_TAG = "PASSWORD_LABEL_TAG"
private const val PASSWORD_VISIBILITY_TAG = "PASSWORD_VISIBILITY_TAG"
private const val PASSWORD_FIELD_TAG = "PASSWORD_FIELD_TAG"

fun StringResource.descTagTriple(): Triple<String, String, String> = this
    .let { runBlocking { getString(it) } }
    .let(String::descTagTriple)

fun String.descTagTriple(): Triple<String, String, String> = Triple(
    first = "${this}_$PASSWORD_LABEL_TAG",
    second = "${this}_$PASSWORD_VISIBILITY_TAG",
    third = "${this}_$PASSWORD_FIELD_TAG"
)

@Composable
fun rememberTagTriple(desc: String): Triple<String, String, String> =
    remember(desc, desc::descTagTriple)

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
) {
    val labelState by remember(label, isError) { mutableStateOf(label) } // workaround for ui-tests
    val (labelTag, visibilityTag, fieldTag) = rememberTagTriple(contentDescription)
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    TextField(
        modifier = modifier.testTag(fieldTag).semantics { contentType = passwordContentType },
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
fun PreviewPasswordField() = PasswordField()
