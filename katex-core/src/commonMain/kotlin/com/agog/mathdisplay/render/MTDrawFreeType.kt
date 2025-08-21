package com.agog.mathdisplay.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import com.agog.mathdisplay.graphics.createImageBitmapFromFreetypeBitmap
import com.agog.mathdisplay.parse.MathDisplayException
import com.agog.mathdisplay.utils.BitmapCache
import com.agog.mathdisplay.utils.MTConfig.IS_DEBUG
import com.pvporbit.freetype.FreeTypeConstants


class MTDrawFreeType(val mathFont: MTFontMathTable) {

    fun drawGlyph(canvas: Canvas, p: Paint, gid: Int, x: Float, y: Float) {
        val face = mathFont.checkFontSize()

        // 先用 NO_RENDER 模式获取字形信息（不加载实际 bitmap）
        if (gid != 0 && face.loadGlyph(gid, FreeTypeConstants.FT_LOAD_NO_BITMAP)) {
            val glyphSlot = face.glyphSlot
            val metrics = glyphSlot?.metrics

            if (metrics != null) {
                // 根据 metrics 估算实际渲染尺寸
                val estimatedWidth = (metrics.width / 64.0f).toInt()
                val estimatedHeight = (metrics.height / 64.0f).toInt()

                // 构建包含估算尺寸的缓存键
                val cacheKey =
                    "$gid-${mathFont.fontName}-${mathFont.fontSize}-${estimatedWidth}x${estimatedHeight}"

                val cachedBitmap = BitmapCache[cacheKey]
                if (cachedBitmap != null) {
                    val offsetX = metrics.horiBearingX / 64.0f
                    val offsetY = metrics.horiBearingY / 64.0f
                    canvas.drawImage(cachedBitmap, Offset(x + offsetX, y - offsetY), p)
                    if (IS_DEBUG) {
                        debugDrawStroke(
                            canvas,
                            x + offsetX,
                            y - offsetY,
                            estimatedWidth,
                            estimatedHeight
                        )
                    }
                    return
                }
            }
        }


        /* load glyph image into the slot and render (erase previous one) */
        if (gid != 0 && face.loadGlyph(gid, FreeTypeConstants.FT_LOAD_RENDER)) {
            val glyphSlot = face.glyphSlot
            val plainBitmap = glyphSlot?.bitmap
            if (plainBitmap != null) {
                if (plainBitmap.width == 0 || plainBitmap.rows == 0) {
                    if (gid != 1 && gid != 33) {
                        throw MathDisplayException("missing glyph slot $gid.")
                    }
                } else {
                    val cacheKey =
                        "$gid-${mathFont.fontName}-${mathFont.fontSize}-${plainBitmap.width}x${plainBitmap.rows}"
                    val cachedBitmap = BitmapCache[cacheKey]
                    val bitmap = cachedBitmap ?: createImageBitmapFromFreetypeBitmap(
                        plainBitmap.width,
                        plainBitmap.rows,
                        plainBitmap.buffer
                    ).also {
                        BitmapCache[cacheKey] = it
                    }
                    val metrics = glyphSlot.metrics!!
                    // 26.6 fixed point integer from freetype
                    val offsetX = metrics.horiBearingX / 64.0f
                    val offsetY = metrics.horiBearingY / 64.0f
                    canvas.drawImage(bitmap, Offset(x + offsetX, y - offsetY), p)
                    if (IS_DEBUG) {
                        debugDrawStroke(
                            canvas,
                            x + offsetX,
                            y - offsetY,
                            plainBitmap.width,
                            plainBitmap.rows
                        )
                    }
                }
            }
        }
    }

    private fun debugDrawStroke(canvas: Canvas, x: Float, y: Float, width: Int, height: Int) {
        canvas.drawRect(
            rect = Rect(
                left = x,
                top = y,
                right = x + width,
                bottom = y + height
            ),
            paint = Paint().apply {
                style = PaintingStyle.Stroke
                color = Color.Red
            }
        )
    }
}