package io.github.darriousliu.katex.mathdisplay.render

import io.github.darriousliu.katex.mathdisplay.parse.MathDisplayException
import io.github.darriousliu.katex.freetype.Face
import io.github.darriousliu.katex.freetype.FreeType
import io.github.darriousliu.katex.freetype.FreeTypeConstants.FT_LOAD_NO_SCALE
import io.github.darriousliu.katex.freetype.MTFreeTypeMathTable

data class MTGlyphPart(
    val glyph: Int = 0,
    val fullAdvance: Float = 0f,
    val startConnectorLength: Float = 0f,
    val endConnectorLength: Float = 0f,
    val isExtender: Boolean = false
)


class BoundingBox() {
    var lowerLeftX: Float = 0.0f
    var lowerLeftY: Float = 0.0f
    var upperRightX: Float = 0.0f
    var upperRightY: Float = 0.0f

    val width: Float
        get() = this.upperRightX - this.lowerLeftX

    val height: Float
        get() = this.upperRightY - this.lowerLeftY


    constructor(minX: Float, minY: Float, maxX: Float, maxY: Float) : this() {
        this.lowerLeftX = minX
        this.lowerLeftY = minY
        this.upperRightX = maxX
        this.upperRightY = maxY
    }

    constructor(numbers: List<Number>) : this() {
        this.lowerLeftX = numbers[0].toFloat()
        this.lowerLeftY = numbers[1].toFloat()
        this.upperRightX = numbers[2].toFloat()
        this.upperRightY = numbers[3].toFloat()
    }

    fun contains(x: Float, y: Float): Boolean {
        return x >= this.lowerLeftX && x <= this.upperRightX && y >= this.lowerLeftY && y <= this.upperRightY
    }

    override fun toString(): String {
        return "[" + this.lowerLeftX + "," + this.lowerLeftY + "," + this.upperRightX + "," + this.upperRightY + "]"
    }
}

class MTFontMathTable {
    val fontSize: Float
    val fontName: String

    val unitsPerEm: Int

    val freeFace: Face
    val freeTypeMathTable: MTFreeTypeMathTable

    constructor(
        fontSize: Float,
        fontName: String,
        fontPath: String
    ) {
        this.fontSize = fontSize
        this.fontName = fontName
//        println("Loading math table from $fontPath")
        /* --- Init FreeType --- */
        /* get singleton */
        val library = FreeType.newLibrary()
            ?: throw MathDisplayException("Error initializing FreeType.")
//        println("FreeType library version: ${library.version}")

        this.freeFace = library.newFace(fontPath, 0)!!
//        println("FreeType face loaded: ${freeFace.familyName} (${freeFace.faceIndex})")
        checkFontSize()
        this.unitsPerEm = freeFace.unitsPerEM
        this.freeTypeMathTable = freeFace.loadMathTable()
    }

    constructor(
        fontSize: Float,
        fontName: String,
        fontData: ByteArray
    ) {
        this.fontSize = fontSize
        this.fontName = fontName
        val library = FreeType.newLibrary()
            ?: throw MathDisplayException("Error initializing FreeType.")
        this.freeFace = library.newFace(fontData, 0)!!
        checkFontSize()
        this.unitsPerEm = freeFace.unitsPerEM
        this.freeTypeMathTable = freeFace.loadMathTable()
    }

    constructor(
        fontSize: Float,
        fontName: String,
        unitsPerEm: Int,
        freeFace: Face,
        freeTypeMathTable: MTFreeTypeMathTable
    ) {
        this.fontSize = fontSize
        this.fontName = fontName
//        println("Using existing math table for $fontName")
        this.unitsPerEm = unitsPerEm
        this.freeFace = freeFace
        this.freeTypeMathTable = freeTypeMathTable
    }

    fun checkFontSize(): Face {
        freeFace.setCharSize(0, (fontSize * 64).toInt(), 0, 0)
        return freeFace
    }

    // Lightweight copy
    fun copyFontTableWithSize(size: Float): MTFontMathTable {
        val copyTable = MTFontMathTable(size, fontName, unitsPerEm, freeFace, freeTypeMathTable)
        return copyTable
    }

    fun getGlyphName(gid: Int): String {
        val g = this.freeFace.getGlyphName(gid)
        return g
    }

    fun getGlyphWithName(glyphName: String): Int {
        val g = this.freeFace.getGlyphIndexByName(glyphName)
        return g
    }

    fun getGlyphForCodepoint(codepoint: Int): Int {
        val g = this.freeFace.getCharIndex(codepoint)
        return g
    }

    fun getAdvancesForGlyphs(glyphs: List<Int>, advances: Array<Float>, count: Int) {
        for (i in 0 until count) {
            if (freeFace.loadGlyph(glyphs[i], FT_LOAD_NO_SCALE)) {
                val glyphSlot = freeFace.glyphSlot
                val a = glyphSlot?.advance
                if (a != null) {
                    advances[i] = fontUnitsToPt(a.x)
                }
            }
        }
    }

    fun unionBounds(u: BoundingBox, b: BoundingBox) {
        u.lowerLeftX = minOf(u.lowerLeftX, b.lowerLeftX)
        u.lowerLeftY = minOf(u.lowerLeftY, b.lowerLeftY)
        u.upperRightX = maxOf(u.upperRightX, b.upperRightX)
        u.upperRightY = maxOf(u.upperRightY, b.upperRightY)
    }

    //  Good description and picture
    // https://www.freetype.org/freetype2/docs/glyphs/glyphs-3.html

    fun getBoundingRectsForGlyphs(
        glyphs: List<Int>,
        boundingRects: Array<BoundingBox?>?,
        count: Int
    ): BoundingBox {
        val enclosing = BoundingBox()

        for (i in 0 until count) {
            if (freeFace.loadGlyph(glyphs[i], FT_LOAD_NO_SCALE)) {
                val nb = BoundingBox()
                val glyphSlot = freeFace.glyphSlot
                val m = glyphSlot?.metrics!!

                val w = fontUnitsToPt(m.width)
                val h = fontUnitsToPt(m.height)
                //val HoriAdvance = fontUnitsToPt(m.getHoriAdvance())
                //val VertAdvance = fontUnitsToPt(m.getVertAdvance())
                val horiBearingX = fontUnitsToPt(m.horiBearingX)
                val horiBearingY = fontUnitsToPt(m.horiBearingY)
                //val VertBearingX = fontUnitsToPt(m.getVertBearingX())
                //val VertBearingY = fontUnitsToPt(m.getVertBearingY())
                //println("$a $m $w $h $HoriAdvance $VertAdvance $horiBearingX $horiBearingY $VertBearingX $VertBearingY")
                nb.lowerLeftX = horiBearingX
                nb.lowerLeftY = horiBearingY - h
                nb.upperRightX = horiBearingX + w
                nb.upperRightY = horiBearingY
                //println("nb $nb")

                unionBounds(enclosing, nb)
                if (boundingRects != null) {
                    boundingRects[i] = nb
                }
            }

        }
        return enclosing
    }

    private fun fontUnitsToPt(fontUnits: Long): Float {
        return fontUnits * fontSize / unitsPerEm
    }

    private fun fontUnitsToPt(fontUnits: Int): Float {
        return fontUnits * fontSize / unitsPerEm
    }

    fun fontUnitsBox(b: BoundingBox): BoundingBox {
        val rb = BoundingBox()
        rb.lowerLeftX = fontUnitsToPt(b.lowerLeftX.toInt())
        rb.lowerLeftY = fontUnitsToPt(b.lowerLeftY.toInt())
        rb.upperRightX = fontUnitsToPt(b.upperRightX.toInt())
        rb.upperRightY = fontUnitsToPt(b.upperRightY.toInt())
        return rb
    }


    fun muUnit(): Float {
        return fontSize / 18
    }

    fun constantFromTable(constName: String): Float {
        return fontUnitsToPt(freeTypeMathTable.getConstant(constName))
    }


    fun percentFromTable(percentName: String): Float {
        return freeTypeMathTable.getConstant(percentName) / 100.0f
    }

    // MARK: - Fractions
    val fractionNumeratorDisplayStyleShiftUp: Float
        get() = constantFromTable("FractionNumeratorDisplayStyleShiftUp")
    val fractionNumeratorShiftUp: Float
        get() = constantFromTable("FractionNumeratorShiftUp")
    val fractionDenominatorDisplayStyleShiftDown: Float
        get() = constantFromTable("FractionDenominatorDisplayStyleShiftDown")
    val fractionDenominatorShiftDown: Float
        get() = constantFromTable("FractionDenominatorShiftDown")
    val fractionNumeratorDisplayStyleGapMin: Float
        get() = constantFromTable("FractionNumDisplayStyleGapMin")
    val fractionNumeratorGapMin: Float
        get() = constantFromTable("FractionNumeratorGapMin")
    val fractionDenominatorDisplayStyleGapMin: Float
        get() = constantFromTable("FractionDenomDisplayStyleGapMin")
    val fractionDenominatorGapMin: Float
        get() = constantFromTable("FractionDenominatorGapMin")
    val fractionRuleThickness: Float
        get() = constantFromTable("FractionRuleThickness")
    val skewedFractionHorizontalGap: Float
        get() = constantFromTable("SkewedFractionHorizontalGap")
    val skewedFractionVerticalGap: Float
        get() = constantFromTable("SkewedFractionVerticalGap")

    // MARK: - Non-standard
    /**
     * FractionDelimiterSize and FractionDelimiterDisplayStyleSize are not constants
     * specified in the OpenType Math specification. Rather these are proposed LuaTeX extensions
     * for the TeX parameters \sigma_20 (delim1) and \sigma_21 (delim2). Since these do not
     * exist in the fonts that we have, we use the same approach as LuaTeX and use the fontSize
     * to determine these values. The constants used are the same as LuaTeX and KaTeX and match the
     * metrics values of the original TeX fonts.
     * Note: An alternative approach is to use DelimitedSubFormulaMinHeight for \sigma21 and use a factor
     * of 2 to get \sigma 20 as proposed in Vieth paper.
     * The XeTeX implementation sets \sigma21 = fontSize and \sigma20 = DelimitedSubFormulaMinHeight which
     * will produce smaller delimiters.
     * Of all the approaches we've implemented LuaTeX's approach since it mimics LaTeX most accurately.
     */
    val fractionDelimiterSize: Float
        get() = 1.01f * fontSize

    /**
     * Modified constant from 2.4 to 2.39, it matches KaTeX and looks better.
     */
    val fractionDelimiterDisplayStyleSize: Float
        get() = 2.39f * fontSize

    // MARK: - super/sub scripts
    val superscriptShiftUp: Float
        get() = constantFromTable("SuperscriptShiftUp")
    val superscriptShiftUpCramped: Float
        get() = constantFromTable("SuperscriptShiftUpCramped")
    val subscriptShiftDown: Float
        get() = constantFromTable("SubscriptShiftDown")
    val superscriptBaselineDropMax: Float
        get() = constantFromTable("SuperscriptBaselineDropMax")
    val subscriptBaselineDropMin: Float
        get() = constantFromTable("SubscriptBaselineDropMin")
    val superscriptBottomMin: Float
        get() = constantFromTable("SuperscriptBottomMin")
    val subscriptTopMax: Float
        get() = constantFromTable("SubscriptTopMax")
    val subSuperscriptGapMin: Float
        get() = constantFromTable("SubSuperscriptGapMin")
    val superscriptBottomMaxWithSubscript: Float
        get() = constantFromTable("SuperscriptBottomMaxWithSubscript")
    val spaceAfterScript: Float
        get() = constantFromTable("SpaceAfterScript")

    // MARK: - radicals
    val radicalRuleThickness: Float
        get() = constantFromTable("RadicalRuleThickness")
    val radicalExtraAscender: Float
        get() = constantFromTable("RadicalExtraAscender")
    val radicalVerticalGap: Float
        get() = constantFromTable("RadicalVerticalGap")
    val radicalDisplayStyleVerticalGap: Float
        get() = constantFromTable("RadicalDisplayStyleVerticalGap")
    val radicalKernBeforeDegree: Float
        get() = constantFromTable("RadicalKernBeforeDegree")
    val radicalKernAfterDegree: Float
        get() = constantFromTable("RadicalKernAfterDegree")
    val radicalDegreeBottomRaisePercent: Float
        get() = percentFromTable("RadicalDegreeBottomRaisePercent")

    // MARK: - Limits
    val upperLimitGapMin: Float
        get() = constantFromTable("UpperLimitGapMin")
    val upperLimitBaselineRiseMin: Float
        get() = constantFromTable("UpperLimitBaselineRiseMin")
    val lowerLimitGapMin: Float
        get() = constantFromTable("LowerLimitGapMin")
    val lowerLimitBaselineDropMin: Float
        get() = constantFromTable("LowerLimitBaselineDropMin")

    // not present in OpenType fonts.
    val limitExtraAscenderDescender: Float
        get() = 0.0f

    // MARK: - Constants
    val axisHeight: Float
        get() = constantFromTable("AxisHeight")
    val scriptScaleDown: Float
        get() = percentFromTable("ScriptPercentScaleDown")
    val scriptScriptScaleDown: Float
        get() = percentFromTable("ScriptScriptPercentScaleDown")
    val mathLeading: Float
        get() = constantFromTable("MathLeading")
    val delimitedSubFormulaMinHeight: Float
        get() = constantFromTable("DelimitedSubFormulaMinHeight")

    // MARK: - Accent
    val accentBaseHeight: Float
        get() = constantFromTable("AccentBaseHeight")
    val flattenedAccentBaseHeight: Float
        get() = constantFromTable("FlattenedAccentBaseHeight")

    // Large Operators

    val displayOperatorMinHeight: Float
        get() = constantFromTable("DisplayOperatorMinHeight")

    // MARK: - Overline
    val overbarExtraAscender: Float
        get() = constantFromTable("OverbarExtraAscender")
    val overbarRuleThickness: Float
        get() = constantFromTable("OverbarRuleThickness")
    val overbarVerticalGap: Float
        get() = constantFromTable("OverbarVerticalGap")

    // MARK: - Underline
    val underbarExtraDescender: Float
        get() = constantFromTable("UnderbarExtraDescender")
    val underbarRuleThickness: Float
        get() = constantFromTable("UnderbarRuleThickness")
    val underbarVerticalGap: Float
        get() = constantFromTable("UnderbarVerticalGap")

    // MARK: - Stacks
    val stackTopDisplayStyleShiftUp: Float
        get() = constantFromTable("StackTopDisplayStyleShiftUp")
    val stackTopShiftUp: Float
        get() = constantFromTable("StackTopShiftUp")
    val stackBottomDisplayStyleShiftDown: Float
        get() = constantFromTable("StackBottomDisplayStyleShiftDown")
    val stackBottomShiftDown: Float
        get() = constantFromTable("StackBottomShiftDown")
    val stackDisplayStyleGapMin: Float
        get() = constantFromTable("StackDisplayStyleGapMin")
    val stackGapMin: Float
        get() = constantFromTable("StackGapMin")
    val stretchStackBottomShiftDown: Float
        get() = constantFromTable("StretchStackBottomShiftDown")
    val stretchStackGapAboveMin: Float
        get() = constantFromTable("StretchStackGapAboveMin")
    val stretchStackGapBelowMin: Float
        get() = constantFromTable("StretchStackGapBelowMin")
    val stretchStackTopShiftUp: Float
        get() = constantFromTable("StretchStackTopShiftUp")

    /**
     * 获取指定字形的垂直变体列表。
     *
     * @param glyph 表示字形的CGGlyph对象。
     * @return 包含该字形所有垂直变体的整型列表。
     */
    fun getVerticalVariantsForGlyph(glyph: CGGlyph): List<Int> {
        return freeTypeMathTable.getVerticalVariantsForGlyph(glyph.gid)
    }

    fun getHorizontalVariantsForGlyph(glyph: CGGlyph): List<Int> {
        return freeTypeMathTable.getHorizontalVariantsForGlyph(glyph.gid)
    }

    fun getLargerGlyph(glyph: Int): Int {
        val glyphName = getGlyphName(glyph)
        // Find the first variant with a different name.
        val variantGlyphs = freeTypeMathTable.getVerticalVariantsForGlyph(glyph)
        for (vGlyph in variantGlyphs) {
            val vName = getGlyphName(vGlyph)
            if (vName != glyphName) {
                return getGlyphWithName(vName)
            }
        }
        // We did not find any variants of this glyph so return it.
        return glyph
    }

    // Italic Correction
    fun getItalicCorrection(gid: Int): Float {
        return fontUnitsToPt(freeTypeMathTable.getItalicCorrection(gid))
    }

    // Top Accent Adjustment
    fun getTopAccentAdjustment(glyph: Int): Float {
        val value = freeTypeMathTable.getTopAccentAttachment(glyph)
        return if (value != null) {
            fontUnitsToPt(value)
        } else {
            // testWideAccent test case covers this

            // If no top accent is defined then it is the center of the advance width.
            val glyphs = arrayOf(glyph)
            val advances = arrayOf(0.0f)

            getAdvancesForGlyphs(glyphs.toList(), advances, 1)
            advances[0] / 2
        }
    }

    // Glyph Assembly
    val minConnectorOverlap: Float
        get() = fontUnitsToPt(freeTypeMathTable.minConnectorOverlap)


    fun getVerticalGlyphAssemblyForGlyph(glyph: Int): List<MTGlyphPart>? {
        val assemblyInfo = freeTypeMathTable.getVerticalGlyphAssemblyForGlyph(glyph)

        if (assemblyInfo == null) {
            // No vertical assembly defined for glyph
            return null
        }

        val rv = mutableListOf<MTGlyphPart>()
        for (pi in assemblyInfo) {
            val part = MTGlyphPart(
                glyph = pi.glyph,
                fullAdvance = fontUnitsToPt(pi.fullAdvance),
                startConnectorLength = fontUnitsToPt(pi.startConnectorLength),
                endConnectorLength = fontUnitsToPt(pi.endConnectorLength),
                isExtender = pi.partFlags == 1
            )
            rv.add(part)
        }
        return rv
    }
}
