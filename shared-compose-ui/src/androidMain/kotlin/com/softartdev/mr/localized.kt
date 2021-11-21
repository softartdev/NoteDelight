package com.softartdev.mr

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc

@Composable
actual fun StringResource.localized(): String = desc().toString(
    context = LocalContext.current
)