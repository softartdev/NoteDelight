package com.softartdev.mr

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc

//TODO remove when moko-resources will support compose
var mokoResourcesContext: Context? = null

actual fun StringResource.contextLocalized(): String = requireNotNull(mokoResourcesContext) {
    "Android application context for moko-resources must not be null"
}.getString(resourceId)

@Composable
actual fun StringResource.composeLocalized(): String = stringResource(id = this.resourceId)

@Composable
actual fun StringDesc.asString(): String = toString(context = LocalContext.current)