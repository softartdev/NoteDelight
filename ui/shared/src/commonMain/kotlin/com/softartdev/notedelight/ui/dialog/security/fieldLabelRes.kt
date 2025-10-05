package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.confirm_password
import notedelight.ui.shared.generated.resources.empty_password
import notedelight.ui.shared.generated.resources.enter_new_password
import notedelight.ui.shared.generated.resources.enter_old_password
import notedelight.ui.shared.generated.resources.enter_password
import notedelight.ui.shared.generated.resources.incorrect_password
import notedelight.ui.shared.generated.resources.passwords_do_not_match
import notedelight.ui.shared.generated.resources.repeat_new_password
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val FieldLabel.resString: String
    @Composable get() = stringResource(this.res)

private val FieldLabel.res: StringResource
    get() = when (this) {
        FieldLabel.ENTER_PASSWORD -> Res.string.enter_password
        FieldLabel.CONFIRM_PASSWORD -> Res.string.confirm_password
        FieldLabel.ENTER_OLD_PASSWORD -> Res.string.enter_old_password
        FieldLabel.ENTER_NEW_PASSWORD -> Res.string.enter_new_password
        FieldLabel.REPEAT_NEW_PASSWORD -> Res.string.repeat_new_password
        FieldLabel.INCORRECT_PASSWORD -> Res.string.incorrect_password
        FieldLabel.EMPTY_PASSWORD -> Res.string.empty_password
        FieldLabel.PASSWORDS_NOT_MATCH -> Res.string.passwords_do_not_match
    }
