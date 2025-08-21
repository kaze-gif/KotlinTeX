package com.agog.mathdisplay.render

import com.pvporbit.freetype.Utils

class MTFont {
    val fontSize: Float
    val mathTable: MTFontMathTable

    constructor(fontSize: Float, fontName: String) {
        this.fontSize = fontSize
        this.mathTable = MTFontMathTable(fontSize, fontName, "files/fonts/$fontName.otf")
    }

    constructor(fontSize: Float, mathTable: MTFontMathTable) {
        this.fontSize = fontSize
        this.mathTable = mathTable
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
        val copyFont = MTFont(size, mathTable.copyFontTableWithSize(size))
        return copyFont
    }
}