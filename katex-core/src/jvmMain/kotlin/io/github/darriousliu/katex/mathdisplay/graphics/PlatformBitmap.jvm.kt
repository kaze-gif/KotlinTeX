package io.github.darriousliu.katex.mathdisplay.graphics

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposeImageBitmap
import io.github.darriousliu.katex.freetype.NativeBinaryBuffer
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ImageInfo

internal actual fun createImageBitmapFromFreetypeBitmap(
    width: Int,
    height: Int,
    buffer: NativeBinaryBuffer
): ImageBitmap {
    val imageInfo = ImageInfo.makeA8(width, height)
    val bitmap = Bitmap()
    bitmap.allocPixels(imageInfo)

    // 将 ByteBuffer 数据复制到 Skia Bitmap
    val pixels = buffer.toByteArray()
    bitmap.installPixels(pixels)

    return bitmap.asComposeImageBitmap()
}

internal actual fun createPlatformPaint(): Paint {
    return Paint().apply {
        isAntiAlias = true
    }
}