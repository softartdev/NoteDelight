package com.softartdev.notedelight.ui.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.FileLock: ImageVector
    get() {
        if (_fileLock == null) {
            _fileLock = materialIcon(name = "Filled.FileLock") {
                materialPath {
                    moveTo(6f, 2f)
                    curveTo(4.89f, 2f, 4f, 2.9f, 4f, 4f)
                    verticalLineTo(20f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6f, 22f)
                    horizontalLineTo(18f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 20f, 20f)
                    verticalLineTo(8f)
                    lineTo(14f, 2f)
                    horizontalLineTo(6f)
                    moveTo(13f, 3.5f)
                    lineTo(18.5f, 9f)
                    horizontalLineTo(13f)
                    verticalLineTo(3.5f)
                    moveTo(12f, 11f)
                    arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 14f)
                    verticalLineTo(15f)
                    horizontalLineTo(16f)
                    verticalLineTo(19f)
                    horizontalLineTo(8f)
                    verticalLineTo(15f)
                    horizontalLineTo(9f)
                    verticalLineTo(14f)
                    curveTo(9f, 12.36f, 10.34f, 11f, 12f, 11f)
                    moveTo(12f, 13f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 11f, 14f)
                    verticalLineTo(15f)
                    horizontalLineTo(13f)
                    verticalLineTo(14f)
                    curveTo(13f, 13.47f, 12.55f, 13f, 12f, 13f)
                    close()
                }
            }
        }
        return _fileLock!!
    }

private var _fileLock: ImageVector? = null
