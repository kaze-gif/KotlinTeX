package com.agog.mathdisplay.render

import com.pvporbit.freetype.Utils

class MTFont(
    val name: String,
    val fontSize: Float,
    isCopy: Boolean = false
) {
    lateinit var mathTable: MTFontMathTable

    init {
        val fontPath = "files/fonts/$name.otf"

        if (!isCopy) {
            mathTable = MTFontMathTable(this, fontPath)
        }
    }


    fun findGlyphForCharacterAtIndex(index: Int, str: String): CGGlyph {
        // Do we need to check with our font to see if this glyph is in the font?
        val codepoint = Utils.codePointAt(str, index)
        val gid = mathTable.getGlyphForCodepoint(codepoint)
        return CGGlyph(gid)
    }

    fun getGidListForString(str: String): List<Int> {
        val ret = MutableList(0) { 0 }

        var i = 0
        while (i < str.length) {
            val codepoint = Utils.codePointAt(str, i)
            i += Utils.charCount(codepoint)
            val gid = mathTable.getGlyphForCodepoint(codepoint)
            if (gid == 0) {
                println("getGidListForString codepoint ${codepoint.toHexString()} ${codepoint.toChar()} mapped to missing glyph")
            }
            ret.add(gid)
        }
        return ret
    }


    fun copyFontWithSize(size: Float): MTFont {
        val copyFont = MTFont(this.name, size, true)
        copyFont.mathTable = this.mathTable.copyFontTableWithSize(size)
        return copyFont
    }


    fun getGlyphName(gid: Int): String {
        return mathTable.getGlyphName(gid)
    }

    fun getGlyphWithName(glyphName: String): Int {
        return mathTable.getGlyphWithName(glyphName)
    }

}