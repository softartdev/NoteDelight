package com.softartdev.mr

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource

expect fun StringResource.contextLocalized(): String

@Composable
expect fun StringResource.composeLocalized(): String
