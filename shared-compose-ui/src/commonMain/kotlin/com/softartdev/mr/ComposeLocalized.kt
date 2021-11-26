package com.softartdev.mr

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Composable
fun StringResource.localized(): String = desc().asString()

@Composable
expect fun StringDesc.asString(): String