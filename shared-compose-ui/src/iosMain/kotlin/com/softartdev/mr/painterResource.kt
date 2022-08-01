package com.softartdev.mr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import dev.icerock.moko.resources.ImageResource

@Composable
actual fun painterResource(imageResource: ImageResource): Painter {
    // TODO Bundle pics and show images properly
    return remember(imageResource) {
        object : Painter() {
            override val intrinsicSize: Size = Size(16f, 16f)
            override fun DrawScope.onDraw() = drawRect(color = Color.Green)
        }
    }
}
