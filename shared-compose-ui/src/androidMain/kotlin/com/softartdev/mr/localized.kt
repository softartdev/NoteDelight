package com.softartdev.mr

import android.content.Context
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.desc

object MokoResHolder {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context
    }

    fun requireContext(): Context = requireNotNull(appContext) {
        "Android application context for moko-resources must not be null"
    }
}

actual fun StringResource.contextLocalized(): String =
    desc().toString(context = MokoResHolder.requireContext())

@Composable
actual fun StringResource.composeLocalized(): String = stringResource(resource = this)
