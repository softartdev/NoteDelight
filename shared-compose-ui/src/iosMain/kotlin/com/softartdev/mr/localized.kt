package com.softartdev.mr

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc

actual fun StringResource.contextLocalized(): String = this.desc().localized()

@Composable
actual fun StringResource.composeLocalized(): String = this.contextLocalized()