package com.softartdev.mr

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc

actual fun StringResource.contextLocalized(): String = this.localized()

@Composable
actual fun StringResource.composeLocalized(): String = this.localized()

@Composable
actual fun StringDesc.asString(): String = this.localized()