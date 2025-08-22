package io.github.darriousliu.katex.mathdisplay.graphics

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import io.github.darriousliu.katex.freetype.NativeBinaryBuffer

internal actual fun createImageBitmapFromFreetypeBitmap(
    width: Int,
    height: Int,
    buffer: NativeBinaryBuffer
): ImageBitmap {
    val bitmap = createBitmap(width, height, Bitmap.Config.ALPHA_8)
    bitmap.copyPixelsFromBuffer(buffer.byteBuffer)
    return bitmap.asImageBitmap()
}

internal actual fun createPlatformPaint(): Paint {
    val paint = android.graphics.Paint()
    paint.apply {
        isAntiAlias = true
        isSubpixelText = true
        isLinearText = true
    }
    return paint.asComposePaint()
}