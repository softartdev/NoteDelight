package com.softartdev.mr

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import dev.icerock.moko.resources.ImageResource

@Composable
actual fun painterResource(imageResource: ImageResource): Painter = painterResource(
    id = imageResource.drawableResId
)