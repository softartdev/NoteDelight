package com.softartdev.mr

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import dev.icerock.moko.resources.ImageResource

@Composable
expect fun painterResource(imageResource: ImageResource): Painter