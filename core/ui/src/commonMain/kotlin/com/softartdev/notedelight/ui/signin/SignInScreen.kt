@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillManager
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.signin.SignInAction
import com.softartdev.notedelight.presentation.signin.SignInResult
import com.softartdev.notedelight.presentation.signin.SignInViewModel
import com.softartdev.notedelight.ui.PasswordField
import com.softartdev.notedelight.ui.TooltipIconButton
import com.softartdev.notedelight.util.SIGN_IN_BIOMETRIC_BUTTON_TAG
import com.softartdev.notedelight.util.SIGN_IN_BUTTON_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_FIELD_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_LABEL_TAG
import com.softartdev.notedelight.util.SIGN_IN_PASSWORD_VISIBILITY_TAG
import com.softartdev.notedelight.util.SIGN_IN_SETTINGS_BUTTON_TAG
import notedelight.core.ui.generated.resources.Res
import notedelight.core.ui.generated.resources.app_name
import notedelight.core.ui.generated.resources.biometric_error
import notedelight.core.ui.generated.resources.biometric_prompt_negative_button
import notedelight.core.ui.generated.resources.biometric_prompt_subtitle
import notedelight.core.ui.generated.resources.biometric_prompt_title
import notedelight.core.ui.generated.resources.biometric_signin_button
import notedelight.core.ui.generated.resources.empty_password
import notedelight.core.ui.generated.resources.enter_password
import notedelight.core.ui.generated.resources.incorrect_password
import notedelight.core.ui.generated.resources.settings
import notedelight.core.ui.generated.resources.sign_in
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreen(signInViewModel: SignInViewModel) {
    val signInResultState: State<SignInResult> = signInViewModel.stateFlow.collectAsState()
    val passwordState: MutableState<String> = remember { mutableStateOf("") }
    val autofillManager: AutofillManager? = LocalAutofillManager.current
    LaunchedEffect(key1 = signInViewModel, key2 = autofillManager) {
        signInViewModel.autofillManager = autofillManager
    }
    LaunchedEffect(signInViewModel) {
        signInViewModel.onAction(SignInAction.RefreshBiometric)
    }
    SignInScreenBody(
        showLoading = signInResultState.value.state is SignInResult.State.Progress,
        passwordState = passwordState,
        labelResource = when (signInResultState.value.state) {
            is SignInResult.State.Error.EmptyPass -> Res.string.empty_password
            is SignInResult.State.Error.IncorrectPass -> Res.string.incorrect_password
            is SignInResult.State.Error.Biometric -> Res.string.biometric_error
            else -> Res.string.enter_password
        },
        isError = signInResultState.value.state is SignInResult.State.Error,
        biometricVisible = signInResultState.value.biometricVisible,
        onAction = signInViewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreenBody(
    showLoading: Boolean = true,
    passwordState: MutableState<String> = mutableStateOf("password"),
    labelResource: StringResource = Res.string.enter_password,
    isError: Boolean = false,
    biometricVisible: Boolean = false,
    onAction: (SignInAction) -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.app_name)) },
            actions = {
                TooltipIconButton(
                    imageVector = Icons.Default.Settings,
                    onClick = { onAction(SignInAction.OnSettingsClick) },
                    testTag = SIGN_IN_SETTINGS_BUTTON_TAG,
                    label = stringResource(Res.string.settings),
                )
            }
        )
    },
) { paddingValues: PaddingValues ->
    AdaptiveFrame(modifier = Modifier.padding(paddingValues)) { framePaddingValues: PaddingValues ->
        if (showLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        Column(modifier = Modifier.padding(framePaddingValues)) {
            PasswordField(
                modifier = Modifier.fillMaxWidth(),
                passwordState = passwordState,
                label = stringResource(labelResource),
                isError = isError,
                contentDescription = stringResource(Res.string.enter_password),
                imeAction = ImeAction.Go,
                keyboardActions = KeyboardActions { onAction(SignInAction.OnSignInClick(passwordState.value)) },
                labelTag = SIGN_IN_PASSWORD_LABEL_TAG,
                visibilityTag = SIGN_IN_PASSWORD_VISIBILITY_TAG,
                fieldTag = SIGN_IN_PASSWORD_FIELD_TAG,
            )
            Button(
                modifier = Modifier
                    .testTag(SIGN_IN_BUTTON_TAG)
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = { onAction(SignInAction.OnSignInClick(passwordState.value)) },
            ) { Text(text = stringResource(Res.string.sign_in)) }
            if (biometricVisible) {
                val title = stringResource(Res.string.biometric_prompt_title)
                val subtitle = stringResource(Res.string.biometric_prompt_subtitle)
                val negative = stringResource(Res.string.biometric_prompt_negative_button)
                OutlinedButton(
                    modifier = Modifier
                        .testTag(SIGN_IN_BIOMETRIC_BUTTON_TAG)
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    onClick = { onAction(SignInAction.OnBiometricClick(title, subtitle, negative)) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(text = stringResource(Res.string.biometric_signin_button))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignInScreen() = SignInScreenBody(biometricVisible = true)
