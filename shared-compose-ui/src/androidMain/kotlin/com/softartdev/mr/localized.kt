package com.softartdev.mr

import android.content.Context
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.desc
import org.koin.java.KoinJavaComponent.get

actual fun StringResource.contextLocalized(): String =
    desc().toString(context = get(Context::class.java))

@Composable
actual fun StringResource.composeLocalized(): String = stringResource(resource = this)
