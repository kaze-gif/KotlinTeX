package io.github.darriousliu.katex.freetype

import io.github.darriousliu.katex.freetype.FreeTypeConstants.FT_Render_Mode

internal class GlyphSlot(pointer: Long) : Pointer(pointer) {
    val bitmap: Bitmap?
        get() {
            val bitmap = FreeType.glyphSlotGetBitmap(pointer)
            if (bitmap == 0L) return null
            return Bitmap(bitmap)
        }

    val linearHoriAdvance: Long
        get() = FreeType.glyphSlotGetLinearHoriAdvance(pointer)

    val linearVertAdvance: Long
        get() = FreeType.glyphSlotGetLinearVertAdvance(pointer)

    val advance: Advance
        get() {
            val array = FreeType.glyphSlotGetAdvance(pointer)
            return Advance(array[0], array[1])
        }

    val format: Int
        get() = FreeType.glyphSlotGetFormat(pointer)

    val bitmapLeft: Int
        get() = FreeType.glyphSlotGetBitmapLeft(pointer)

    val bitmapTop: Int
        get() = FreeType.glyphSlotGetBitmapTop(pointer)

    val metrics: GlyphMetrics?
        get() {
            val metrics = FreeType.glyphSlotGetMetrics(pointer)
            if (metrics == 0L) return null
            return GlyphMetrics(metrics)
        }

    fun renderGlyph(renderMode: FT_Render_Mode): Boolean {
        return FreeType.renderGlyph(pointer, renderMode.ordinal)
    }
}

class Advance(val x: Long, val y: Long) {
    override fun toString(): String {
        return "($x,$y)"
    }
}