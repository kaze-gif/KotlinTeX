package io.github.darriousliu.katex.freetype


internal interface IFreeType {
    fun newLibrary(): Library? {
        val library = FreeType.init()
        if (library == 0L) {
            println("Failed to initialize FreeType2 library.")
            return null
        }
        println("Successfully initialized FreeType2 library.$library")
        return Library(library)
    }

    // ---- Library
    fun init(): Long /* Pointer to FT_Library */
    fun doneFreeType(library: Long): Boolean
    fun libraryVersion(library: Long): LibraryVersion
    fun newMemoryFace(
        library: Long,
        data: NativeBinaryBuffer,
        length: Int,
        faceIndex: Long
    ): Long /* Pointer to FT_Face */

    // ---- Face
    fun loadMathTable(face: Long, data: NativeBinaryBuffer, length: Int): Boolean
    fun faceGetAscender(face: Long): Int
    fun faceGetDescender(face: Long): Int
    fun faceGetFaceFlags(face: Long): Long
    fun faceGetFaceIndex(face: Long): Long
    fun faceGetFamilyName(face: Long): String
    fun faceGetHeight(face: Long): Int
    fun faceGetMaxAdvanceHeight(face: Long): Int
    fun faceGetMaxAdvanceWidth(face: Long): Int
    fun faceGetNumFaces(face: Long): Long
    fun faceGetNumGlyphs(face: Long): Long
    fun faceGetStyleFlags(face: Long): Long
    fun faceGetStyleName(face: Long): String
    fun faceGetUnderlinePosition(face: Long): Int
    fun faceGetUnderlineThickness(face: Long): Int
    fun faceGetUnitsPerEM(face: Long): Int
    fun faceGetGlyph(face: Long): Long /* Pointer to FT_GlyphSlot */
    fun faceGetSize(face: Long): Long /* Pointer to FT_Size */
    fun getTrackKerning(face: Long, pointSize: Int, degree: Int): Long
    fun getKerning(face: Long, left: Char, right: Char, mode: Int): Kerning
    fun doneFace(face: Long): Boolean
    fun referenceFace(face: Long): Boolean
    fun hasKerning(face: Long): Boolean
    fun getPostscriptName(face: Long): String
    fun selectCharMap(face: Long, encoding: Int): Boolean
    fun setCharMap(face: Long, charMap: CharMap): Boolean
    fun faceCheckTrueTypePatents(face: Long): Boolean
    fun faceSetUnpatentedHinting(face: Long, value: Boolean): Boolean
    fun getFirstChar(face: Long): LongArray // [charcode, glyphIndex]
    fun getNextChar(face: Long, charcode: Long): Int
    fun getCharIndex(face: Long, code: Int): Int
    fun getNameIndex(face: Long, name: String): Int
    fun getGlyphName(face: Long, glyphIndex: Int): String
    fun getFSTypeFlags(face: Long): Short
    fun selectSize(face: Long, strikeIndex: Int): Boolean
    fun loadChar(face: Long, c: Char, flags: Int): Boolean
    fun requestSize(face: Long, sizeRequest: SizeRequest): Boolean
    fun setPixelSizes(face: Long, width: Int, height: Int): Boolean
    fun loadGlyph(face: Long, glyphIndex: Int, loadFlags: Int): Boolean
    fun setCharSize(
        face: Long,
        charWidth: Int,
        charHeight: Int,
        horizResolution: Int,
        vertResolution: Int
    ): Boolean

    // ---- Size
    fun sizeGetMetrics(size: Long): Long /* Pointer to SizeMetrics */

    // ---- Size Metrics
    fun sizeMetricsGetAscender(sizeMetrics: Long): Long
    fun sizeMetricsGetDescender(sizeMetrics: Long): Long
    fun sizeMetricsGetHeight(sizeMetrics: Long): Long
    fun sizeMetricsGetMaxAdvance(sizeMetrics: Long): Long
    fun sizeMetricsGetXPPEM(sizeMetrics: Long): Int
    fun sizeMetricsGetXScale(sizeMetrics: Long): Long
    fun sizeMetricsGetYPPEM(sizeMetrics: Long): Int
    fun sizeMetricsGetYScale(sizeMetrics: Long): Long

    // ---- GlyphSlot
    fun glyphSlotGetLinearHoriAdvance(glyphSlot: Long): Long
    fun glyphSlotGetLinearVertAdvance(glyphSlot: Long): Long
    fun glyphSlotGetAdvance(glyphSlot: Long): LongArray
    fun glyphSlotGetFormat(glyphSlot: Long): Int
    fun glyphSlotGetBitmapLeft(glyphSlot: Long): Int
    fun glyphSlotGetBitmapTop(glyphSlot: Long): Int
    fun glyphSlotGetBitmap(glyphSlot: Long): Long /* Pointer to Bitmap */
    fun glyphSlotGetMetrics(glyphSlot: Long): Long /* Pointer to GlyphMetrics */
    fun renderGlyph(glyphSlot: Long, renderMode: Int): Boolean

    // ---- GlyphMetrics
    fun glyphMetricsGetWidth(glyphMetrics: Long): Long
    fun glyphMetricsGetHeight(glyphMetrics: Long): Long
    fun glyphMetricsGetHoriAdvance(glyphMetrics: Long): Long
    fun glyphMetricsGetVertAdvance(glyphMetrics: Long): Long
    fun glyphMetricsGetHoriBearingX(glyphMetrics: Long): Long
    fun glyphMetricsGetHoriBearingY(glyphMetrics: Long): Long
    fun glyphMetricsGetVertBearingX(glyphMetrics: Long): Long
    fun glyphMetricsGetVertBearingY(glyphMetrics: Long): Long

    // ---- Bitmap
    fun bitmapGetWidth(bitmap: Long): Int
    fun bitmapGetRows(bitmap: Long): Int
    fun bitmapGetPitch(bitmap: Long): Int
    fun bitmapGetNumGrays(bitmap: Long): Short
    fun bitmapGetPaletteMode(bitmap: Long): Char
    fun bitmapGetPixelMode(bitmap: Long): Char
    fun bitmapGetBuffer(bitmap: Long): NativeBinaryBuffer

    // ---- Charmap
    fun getCharMapIndex(charMap: Long): Int

    fun newBuffer(size: Int): NativeBinaryBuffer

    fun fillBuffer(bytes: ByteArray, buffer: NativeBinaryBuffer, length: Int) {
        buffer.fill(bytes)
    }

    fun deleteBuffer(buffer: NativeBinaryBuffer) {
        buffer.free()
    }
}

internal expect val FreeType: IFreeType