package com.softartdev.notedelight.ui.dialog.security

import androidx.compose.runtime.Composable
import com.softartdev.notedelight.presentation.settings.security.FieldLabel
import notedelight.shared.generated.resources.Res
import notedelight.shared.generated.resources.empty_password
import notedelight.shared.generated.resources.enter_password
import notedelight.shared.generated.resources.incorrect_password
import notedelight.shared.generated.resources.passwords_do_not_match
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val FieldLabel.resString: String
    @Composable get() = stringResource(this.res)

private val FieldLabel.res: StringResource
    get() = when (this) {
        FieldLabel.ENTER -> Res.string.enter_password
        FieldLabel.EMPTY -> Res.string.empty_password
        FieldLabel.INCORRECT -> Res.string.incorrect_password
        FieldLabel.NO_MATCH -> Res.string.passwords_do_not_match
    }
