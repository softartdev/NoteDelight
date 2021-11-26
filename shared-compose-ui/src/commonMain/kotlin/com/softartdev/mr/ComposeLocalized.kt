package com.softartdev.mr

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc

expect fun StringResource.contextLocalized(): String

@Composable
expect fun StringResource.composeLocalized(): String

@Composable
expect fun StringDesc.asString(): String