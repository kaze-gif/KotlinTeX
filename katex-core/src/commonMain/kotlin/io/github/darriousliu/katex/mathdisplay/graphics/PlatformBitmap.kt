package io.github.darriousliu.katex.mathdisplay.graphics

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import io.github.darriousliu.katex.freetype.NativeBinaryBuffer

/**
 * 从 FreeType 位图创建 Compose ImageBitmap
 */
internal expect fun createImageBitmapFromFreetypeBitmap(
    width: Int,
    height: Int,
    buffer: NativeBinaryBuffer
): ImageBitmap

internal expect fun createPlatformPaint(): Paint