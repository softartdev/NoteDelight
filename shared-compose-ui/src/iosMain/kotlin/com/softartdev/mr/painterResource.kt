package com.softartdev.mr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.icerock.moko.resources.ImageResource
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UByteVarOf
import kotlinx.cinterop.readBytes
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorAlphaType.*
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.*
import platform.CoreGraphics.CGImageAlphaInfo.*
import platform.UIKit.UIImage
import platform.darwin.UInt8
import platform.posix.size_t

@Composable
actual fun painterResource(imageResource: ImageResource): Painter = remember(
    key1 = imageResource.assetImageName
) {
    val uiImage: UIImage = imageResource.toUIImage()!!
    val skiaImage: Image = uiImage.toSkiaImage()
    val composeImageBitmap: ImageBitmap = skiaImage.toComposeImageBitmap()
    return@remember BitmapPainter(image = composeImageBitmap)
}

// TODO: Add support for remaining color spaces when the Skia library supports them.
internal fun UIImage.toSkiaImage(): Image {
    val imageRef = CGImageCreateCopyWithColorSpace(this.CGImage, CGColorSpaceCreateDeviceRGB())
    requireNotNull(imageRef)
    val width = CGImageGetWidth(imageRef).toInt()
    val height = CGImageGetHeight(imageRef).toInt()

    val bytesPerRow: size_t = CGImageGetBytesPerRow(imageRef)
    val data = CGDataProviderCopyData(CGImageGetDataProvider(imageRef))
    val bytePointer: CPointer<UByteVarOf<UInt8>> = CFDataGetBytePtr(data)!!
    val length = CFDataGetLength(data)
    val alphaType: ColorAlphaType = when (CGImageGetAlphaInfo(imageRef)) {
        kCGImageAlphaPremultipliedFirst, kCGImageAlphaPremultipliedLast -> PREMUL
        kCGImageAlphaFirst, kCGImageAlphaLast -> UNPREMUL
        kCGImageAlphaNone, kCGImageAlphaNoneSkipFirst, kCGImageAlphaNoneSkipLast -> OPAQUE
        else -> UNKNOWN
    }
    val byteArray: ByteArray = bytePointer.readBytes(count = length.toInt())
    CFRelease(data)
    CFRelease(imageRef)

    return Image.makeRaster(
        imageInfo = ImageInfo(
            width = width,
            height = height,
            colorType = ColorType.RGBA_8888,
            alphaType = alphaType
        ),
        bytes = byteArray,
        rowBytes = bytesPerRow.toInt(),
    )
}
