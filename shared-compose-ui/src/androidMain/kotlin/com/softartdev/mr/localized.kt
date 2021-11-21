package com.softartdev.mr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc

@Composable
@ReadOnlyComposable
actual fun StringResource.localized(): String = stringResource(id = resourceId)
/*
@Composable
actual fun StringResource.localized(): String = desc().toString(
    context = LocalContext.current
)*/