package com.softartdev.mr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.icerock.moko.resources.StringResource

@Composable
@ReadOnlyComposable
actual fun StringResource.localized(): String = localized()