package com.softartdev.notedelight.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.softartdev.annotation.Preview
import com.softartdev.mr.composeLocalized
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.MR
import dev.icerock.moko.resources.StringResource

private const val PASSWORD_LABEL_TAG = "PASSWORD_LABEL_TAG"
private const val PASSWORD_VISIBILITY_TAG = "PASSWORD_VISIBILITY_TAG"
private const val PASSWORD_FIELD_TAG = "PASSWORD_FIELD_TAG"

fun StringResource.descTagTriple(): Triple<String, String, String> = this
    .let(StringResource::contextLocalized)
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
    label: String = MR.strings.enter_password.composeLocalized(),
    isError: Boolean = true,
    contentDescription: String = MR.strings.enter_password.composeLocalized(),
) {
    val labelState by remember(label, isError) { mutableStateOf(label) } // workaround for ui-tests
    val (labelTag, visibilityTag, fieldTag) = rememberTagTriple(contentDescription)
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    TextField(
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
        modifier = modifier.testTag(fieldTag),
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        trailingIcon = { if (isError) Icon(Icons.Default.Error, Icons.Default.Error.name) else Unit },
    )
}

@Preview
@Composable
fun PreviewPasswordField() = PasswordField()