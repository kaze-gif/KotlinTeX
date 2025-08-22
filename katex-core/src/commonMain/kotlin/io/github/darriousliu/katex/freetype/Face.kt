@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.darriousliu.katex.freetype

import io.github.darriousliu.katex.freetype.FreeTypeConstants.FT_Kerning_Mode

class Face(pointer: Long) : Pointer(pointer) {
    private var data: NativeBinaryBuffer? = null

    constructor(pointer: Long, data: NativeBinaryBuffer?) : this(pointer) {
        this.data = data
    }

    fun delete(): Boolean {
        data?.let { FreeType.deleteBuffer(it) }
        return FreeType.doneFace(pointer)
    }

    fun loadMathTable(): MTFreeTypeMathTable {
        // Temporary buffer size of font.
        val buffer = FreeType.newBuffer(data!!.remaining())
        val fm = MTFreeTypeMathTable(pointer, buffer)
        FreeType.deleteBuffer(buffer)
        return fm
    }

    val ascender: Int
        get() = FreeType.faceGetAscender(pointer)

    val descender: Int
        get() = FreeType.faceGetDescender(pointer)

    val faceFlags: Long
        get() = FreeType.faceGetFaceFlags(pointer)

    val faceIndex: Long
        get() = FreeType.faceGetFaceIndex(pointer)

    val familyName: String
        get() = FreeType.faceGetFamilyName(pointer)

    val height: Int
        get() = FreeType.faceGetHeight(pointer)

    val maxAdvanceHeight: Int
        get() = FreeType.faceGetMaxAdvanceHeight(pointer)

    val maxAdvanceWidth: Int
        get() = FreeType.faceGetMaxAdvanceWidth(pointer)

    val numFaces: Long
        get() = FreeType.faceGetNumFaces(pointer)

    val numGlyphs: Long
        get() = FreeType.faceGetNumGlyphs(pointer)

    val styleFlags: Long
        get() = FreeType.faceGetStyleFlags(pointer)

    val styleName: String
        get() = FreeType.faceGetStyleName(pointer)

    val underlinePosition: Int
        get() = FreeType.faceGetUnderlinePosition(pointer)

    val underlineThickness: Int
        get() = FreeType.faceGetUnderlineThickness(pointer)

    val unitsPerEM: Int
        get() = FreeType.faceGetUnitsPerEM(pointer)

    fun getCharIndex(code: Int): Int {
        return FreeType.getCharIndex(pointer, code)
    }

    fun hasKerning(): Boolean {
        return FreeType.hasKerning(pointer)
    }

    fun selectSize(strikeIndex: Int): Boolean {
        return FreeType.selectSize(pointer, strikeIndex)
    }

    fun setCharSize(
        charWidth: Int,
        charHeight: Int,
        horiResolution: Int,
        vertResolution: Int
    ): Boolean {
        return FreeType.setCharSize(
            pointer,
            charWidth,
            charHeight,
            horiResolution,
            vertResolution
        )
    }

    fun loadGlyph(glyphIndex: Int, flags: Int): Boolean {
        return FreeType.loadGlyph(pointer, glyphIndex, flags)
    }

    fun loadChar(c: Char, flags: Int): Boolean {
        return FreeType.loadChar(pointer, c, flags)
    }

    fun getKerning(left: Char, right: Char): Kerning {
        return getKerning(left, right, FT_Kerning_Mode.FT_KERNING_DEFAULT)
    }

    fun getKerning(left: Char, right: Char, mode: FT_Kerning_Mode): Kerning {
        return FreeType.getKerning(pointer, left, right, mode.ordinal)
    }

    fun setPixelSizes(width: Float, height: Float): Boolean {
        return FreeType.setPixelSizes(pointer, width.toInt(), height.toInt())
    }

    val glyphSlot: GlyphSlot?
        get() {
            val glyph = FreeType.faceGetGlyph(pointer)
            if (glyph == 0L) return null
            return GlyphSlot(glyph)
        }

    val size: Size?
        get() {
            val size = FreeType.faceGetSize(pointer)
            if (size == 0L) return null
            return Size(size)
        }

    fun checkTrueTypePatents(): Boolean {
        return FreeType.faceCheckTrueTypePatents(pointer)
    }

    fun setUnpatentedHinting(newValue: Boolean): Boolean {
        return FreeType.faceSetUnpatentedHinting(pointer, newValue)
    }

    fun referenceFace(): Boolean {
        return FreeType.referenceFace(pointer)
    }

    fun requestSize(sr: SizeRequest): Boolean {
        return FreeType.requestSize(pointer, sr)
    }

    val firstChar: LongArray
        get() = FreeType.getFirstChar(pointer)

    val firstCharAsCharcode: Long
        get() = this.firstChar[0]

    val firstCharAsGlyphIndex: Long
        get() = this.firstChar[1]

    fun getNextChar(charcode: Long): Int { // I will not create getNextCharAsCharcode to do charcode++.
        return FreeType.getNextChar(pointer, charcode)
    }

    fun getGlyphIndexByName(name: String): Int {
        return FreeType.getNameIndex(pointer, name)
    }

    fun getTrackKerning(pointSize: Int, degree: Int): Long {
        return FreeType.getTrackKerning(pointer, pointSize, degree)
    }

    fun getGlyphName(glyphIndex: Int): String {
        return FreeType.getGlyphName(pointer, glyphIndex)
    }

    val postscriptName: String
        get() = FreeType.getPostscriptName(pointer)

    fun selectCharMap(encoding: Int): Boolean {
        return FreeType.selectCharMap(pointer, encoding)
    }

    fun setCharMap(charMap: CharMap): Boolean {
        return FreeType.setCharMap(pointer, charMap)
    }

    val fSTypeFlags: Short
        get() = FreeType.getFSTypeFlags(pointer)

}