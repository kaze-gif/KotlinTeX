package io.github.darriousliu.katex.freetype

/*
  greg@agog.com

  Read the math table from a opentype font and create tables ready for typesetting math.
  freetype doesn't supply a library call to parse this table.
  The whole table is retrieved as a bytearray and then parsed in kotlin


  Best reference I've found for math table format
  https://docs.microsoft.com/en-us/typography/opentype/spec/math


 */
private val constTable = arrayOf(
    "int16", "ScriptPercentScaleDown",
    "int16", "ScriptScriptPercentScaleDown",
    "uint16", "DelimitedSubFormulaMinHeight",
    "uint16", "DisplayOperatorMinHeight",
    "MathValueRecord", "MathLeading",
    "MathValueRecord", "AxisHeight",
    "MathValueRecord", "AccentBaseHeight",
    "MathValueRecord", "FlattenedAccentBaseHeight",
    "MathValueRecord", "SubscriptShiftDown",
    "MathValueRecord", "SubscriptTopMax",
    "MathValueRecord", "SubscriptBaselineDropMin",
    "MathValueRecord", "SuperscriptShiftUp",
    "MathValueRecord", "SuperscriptShiftUpCramped",
    "MathValueRecord", "SuperscriptBottomMin",
    "MathValueRecord", "SuperscriptBaselineDropMax",
    "MathValueRecord", "SubSuperscriptGapMin",
    "MathValueRecord", "SuperscriptBottomMaxWithSubscript",
    "MathValueRecord", "SpaceAfterScript",
    "MathValueRecord", "UpperLimitGapMin",
    "MathValueRecord", "UpperLimitBaselineRiseMin",
    "MathValueRecord", "LowerLimitGapMin",
    "MathValueRecord", "LowerLimitBaselineDropMin",
    "MathValueRecord", "StackTopShiftUp",
    "MathValueRecord", "StackTopDisplayStyleShiftUp",
    "MathValueRecord", "StackBottomShiftDown",
    "MathValueRecord", "StackBottomDisplayStyleShiftDown",
    "MathValueRecord", "StackGapMin",
    "MathValueRecord", "StackDisplayStyleGapMin",
    "MathValueRecord", "StretchStackTopShiftUp",
    "MathValueRecord", "StretchStackBottomShiftDown",
    "MathValueRecord", "StretchStackGapAboveMin",
    "MathValueRecord", "StretchStackGapBelowMin",
    "MathValueRecord", "FractionNumeratorShiftUp",
    "MathValueRecord", "FractionNumeratorDisplayStyleShiftUp",
    "MathValueRecord", "FractionDenominatorShiftDown",
    "MathValueRecord", "FractionDenominatorDisplayStyleShiftDown",
    "MathValueRecord", "FractionNumeratorGapMin",
    "MathValueRecord", "FractionNumDisplayStyleGapMin",
    "MathValueRecord", "FractionRuleThickness",
    "MathValueRecord", "FractionDenominatorGapMin",
    "MathValueRecord", "FractionDenomDisplayStyleGapMin",
    "MathValueRecord", "SkewedFractionHorizontalGap",
    "MathValueRecord", "SkewedFractionVerticalGap",
    "MathValueRecord", "OverbarVerticalGap",
    "MathValueRecord", "OverbarRuleThickness",
    "MathValueRecord", "OverbarExtraAscender",
    "MathValueRecord", "UnderbarVerticalGap",
    "MathValueRecord", "UnderbarRuleThickness",
    "MathValueRecord", "UnderbarExtraDescender",
    "MathValueRecord", "RadicalVerticalGap",
    "MathValueRecord", "RadicalDisplayStyleVerticalGap",
    "MathValueRecord", "RadicalRuleThickness",
    "MathValueRecord", "RadicalExtraAscender",
    "MathValueRecord", "RadicalKernBeforeDegree",
    "MathValueRecord", "RadicalKernAfterDegree",
    "uint16", "RadicalDegreeBottomRaisePercent"
)

class MTFreeTypeMathTable(val pointer: Long, val data: NativeBinaryBuffer) {
    private val constants: HashMap<String, Int> = hashMapOf()
    private val italicsCorrectionInfo: HashMap<Int, Int> = hashMapOf()
    private val topAccentAttachment: HashMap<Int, Int> = hashMapOf()
    private val vertGlyphConstruction: HashMap<Int, MathGlyphConstruction> = hashMapOf()
    private val horizGlyphConstruction: HashMap<Int, MathGlyphConstruction> = hashMapOf()
    var minConnectorOverlap: Int = 0

    init {
        val success = FreeType.loadMathTable(pointer, data, data.remaining())

        if (success) {
            val version = data.int
            if (version == 0x00010000) {
                val mathConstantsOffset = getDataSInt()
                val mathGlyphInfoOffset = getDataSInt()
                val mathVariantsOffset = getDataSInt()
                //println("MathConstants $MathConstants MathGlyphInfo $MathGlyphInfo MathVariants $MathVariants")
                readConstants(mathConstantsOffset)

                // Glyph Info Table
                data.position(mathGlyphInfoOffset)
                val mathItalicsCorrectionInfo = getDataSInt()
                val mathTopAccentAttachment = getDataSInt()
                //val extendedShapeCoverage = getDataSInt()

                // This is unused
                //val mathKernInfo = getDataSInt()

                readMatchedTable(
                    mathGlyphInfoOffset + mathItalicsCorrectionInfo,
                    italicsCorrectionInfo
                )
                readMatchedTable(mathGlyphInfoOffset + mathTopAccentAttachment, topAccentAttachment)

                readVariants(mathVariantsOffset)
            }
        }
    }

    private fun getDataSInt(): Int {
        val v = data.short
        return v.toInt()
    }

    fun getConstant(name: String): Int {
        return constants[name]!!
    }

    fun getItalicCorrection(gid: Int): Int {
        return italicsCorrectionInfo[gid] ?: 0
    }

    fun getTopAccentAttachment(gid: Int): Int? {
        return topAccentAttachment[gid]
    }

    private fun getVariantsForGlyph(
        construction: HashMap<Int, MathGlyphConstruction>,
        gid: Int
    ): List<Int> {
        val v = construction[gid]
        if (v == null || v.variants.isEmpty()) return (listOf(gid))
        val vl = mutableListOf<Int>()
        for (variant in v.variants) {
            vl.add(variant.variantGlyph)
        }
        return vl
    }

    fun getVerticalVariantsForGlyph(gid: Int): List<Int> {
        return getVariantsForGlyph(vertGlyphConstruction, gid)
    }

    fun getHorizontalVariantsForGlyph(gid: Int): List<Int> {
        return getVariantsForGlyph(horizGlyphConstruction, gid)
    }

    fun getVerticalGlyphAssemblyForGlyph(gid: Int): Array<GlyphPartRecord>? {
        val v = vertGlyphConstruction[gid]
        if (v?.assembly == null) return null

        return v.assembly.partRecords
    }


    private fun getDataRecord(): Int {
        val value = getDataSInt()

        @Suppress("UNUSED_VARIABLE", "unused")
        val deviceTable = getDataSInt()
        return value
    }


    // Read either a correction or offset table that has a table of glyphs covered that correspond
    // to an array of MathRecords of the values
    private fun readMatchedTable(offset: Int, table: HashMap<Int, Int>) {
        data.position(offset)
        val coverageOffset = getDataSInt()

        val coverage = readCoverageTable(offset + coverageOffset)

        val count = getDataSInt()
        for (i in 0 until count) {
            // indexed by glyphId
            table[coverage[i]] = getDataRecord()
        }

    }


    private fun readConstants(offset: Int) {
        data.position(offset)

        var i = 0
        while (i < constTable.size) {
            val recordType = constTable[i]
            val recordName = constTable[i + 1]
            when (recordType) {
                "uint16", "int16" -> {
                    val value: Int = getDataSInt()
                    constants[recordName] = value
                }

                else -> {
                    val value: Int = getDataSInt()

                    @Suppress("UNUSED_VARIABLE", "unused")
                    val offset: Int = getDataSInt()
                    constants[recordName] = value
                }
            }
            i += 2
        }
    }


    // https://docs.microsoft.com/en-us/typography/opentype/spec/chapter2
    /**
     * 读取 Coverage 表，该表用于描述字体中一组字形的覆盖范围。
     *
     * @param offset 表的起始偏移位置。
     * @return 一个包含字形 ID 的数组。
     * @throws Exception 当数据格式无效时抛出异常。
     */
    private fun readCoverageTable(offset: Int): Array<Int> {
        val currentPosition = data.position()
        data.position(offset)
        val format: Int = getDataSInt()
        val ra: Array<Int>?

        when (format) {
            1 -> {
                val glyphCount: Int = getDataSInt()
                ra = Array(glyphCount) { 0 }
                for (i in 0 until glyphCount) {
                    ra[i] = getDataSInt()
                }
            }

            2 -> {
                val rangeCount: Int = getDataSInt()
                val rr = mutableListOf<Int>()
                repeat(rangeCount) {
                    val startGlyphID = getDataSInt()
                    val endGlyphID = getDataSInt()
                    var startCoverageIndex = getDataSInt()
                    for (g in startGlyphID..endGlyphID) {
                        rr.add(startCoverageIndex++, g)
                    }
                }
                ra = rr.toTypedArray()
            }

            else -> {
                throw Exception("Invalid coverage format")
            }
        }

        data.position(currentPosition)
        return ra
    }

    class MathGlyphConstruction(
        val assembly: GlyphAssembly?,
        val variants: Array<MathGlyphVariantRecord>
    )

    class MathGlyphVariantRecord(
        val variantGlyph: Int,
        @Suppress("unused") val advanceMeasurement: Int
    )

    class GlyphPartRecord(
        val glyph: Int,
        val startConnectorLength: Int,
        val endConnectorLength: Int,
        val fullAdvance: Int,
        val partFlags: Int
    )

    class GlyphAssembly(
        @Suppress("unused") val italicsCorrection: Int,
        val partRecords: Array<GlyphPartRecord>
    )

    private fun readConstruction(offset: Int): MathGlyphConstruction {
        val currentPosition = data.position()
        data.position(offset)

        val glyphAssemblyOff = getDataSInt()
        val variantCount = getDataSInt()
        val variants = mutableListOf<MathGlyphVariantRecord>()
        for (v in 0 until variantCount) {
            val variantGlyph = getDataSInt()
            val advanceMeasurement = getDataSInt()
            variants.add(v, MathGlyphVariantRecord(variantGlyph, advanceMeasurement))
        }
        val assembly = if (glyphAssemblyOff == 0) null else readAssembly(offset + glyphAssemblyOff)
        val construction = MathGlyphConstruction(assembly, variants.toTypedArray())
        data.position(currentPosition)
        return construction
    }

    private fun readAssembly(offset: Int): GlyphAssembly {
        val currentPosition = data.position()
        data.position(offset)

        val italicsCorrection = getDataRecord()
        val partCount = getDataSInt()
        val parts = mutableListOf<GlyphPartRecord>()

        for (p in 0 until partCount) {
            val glyph = getDataSInt()
            val startConnectorLength = getDataSInt()
            val endConnectorLength = getDataSInt()
            val fullAdvance = getDataSInt()
            val partFlags = getDataSInt()
            parts.add(
                p,
                GlyphPartRecord(
                    glyph,
                    startConnectorLength,
                    endConnectorLength,
                    fullAdvance,
                    partFlags
                )
            )
        }
        val assembly = GlyphAssembly(italicsCorrection, parts.toTypedArray())
        data.position(currentPosition)
        return assembly
    }


    private fun readVariants(offset: Int) {
        data.position(offset)

        this.minConnectorOverlap = getDataSInt()
        val vertGlyphCoverage = getDataSInt()
        val horizGlyphCoverage = getDataSInt()
        val vertGlyphCount = getDataSInt()
        val horizGlyphCount = getDataSInt()

        val vertCoverage = readCoverageTable(offset + vertGlyphCoverage)
        val horizCoverage = readCoverageTable(offset + horizGlyphCoverage)

        for (g in 0 until vertGlyphCount) {
            val glyphConstruction = getDataSInt()
            vertGlyphConstruction[vertCoverage[g]] = readConstruction(offset + glyphConstruction)
        }

        for (g in 0 until horizGlyphCount) {
            val glyphConstruction = getDataSInt()
            horizGlyphConstruction[horizCoverage[g]] = readConstruction(offset + glyphConstruction)
        }
    }
}