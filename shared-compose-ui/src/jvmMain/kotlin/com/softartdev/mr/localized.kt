package com.softartdev.mr

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

actual fun StringResource.contextLocalized(): String = this.localized()

@Composable
actual fun StringResource.composeLocalized(): String = stringResource(resource = this)
