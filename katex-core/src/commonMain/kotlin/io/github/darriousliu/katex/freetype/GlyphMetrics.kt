package io.github.darriousliu.katex.freetype

internal class GlyphMetrics(pointer: Long) : Pointer(pointer) {
    val width: Long
        get() = FreeType.glyphMetricsGetWidth(pointer)

    val height: Long
        get() = FreeType.glyphMetricsGetHeight(pointer)

    val horiAdvance: Long
        get() = FreeType.glyphMetricsGetHoriAdvance(pointer)

    val vertAdvance: Long
        get() = FreeType.glyphMetricsGetVertAdvance(pointer)

    val horiBearingX: Long
        get() = FreeType.glyphMetricsGetHoriBearingX(pointer)

    val horiBearingY: Long
        get() = FreeType.glyphMetricsGetHoriBearingY(pointer)

    val vertBearingX: Long
        get() = FreeType.glyphMetricsGetVertBearingX(pointer)

    val vertBearingY: Long
        get() = FreeType.glyphMetricsGetVertBearingY(pointer)
}