package io.github.darriousliu.katex.freetype

import okio.ByteString.Companion.toByteString
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.freetype.*
import org.lwjgl.util.freetype.FreeType.*
import java.nio.ByteOrder
import kotlin.math.absoluteValue

internal object FreeTypeJvm : IFreeType {
    private val faceCache = mutableMapOf<Long, FT_Face>()

    private fun getFaceFromCache(face: Long): FT_Face {
        return faceCache[face] ?: FT_Face.create(face).also { faceCache[face] = it }
    }

    override fun init(): Long {
        val pp = MemoryUtil.memAllocPointer(1)
        val err = FT_Init_FreeType(pp)
        check(err == FT_Err_Ok) { "Failed to initialize FreeType: " + FT_Error_String(err) }

        val library = pp.get(0)
        return library
    }

    override fun doneFreeType(library: Long): Boolean {
        return FT_Done_FreeType(library) == FT_Err_Ok
    }

    override fun libraryVersion(library: Long): LibraryVersion {
        stackPush().use { stack ->
            val major = stack.mallocInt(1)
            val minor = stack.mallocInt(1)
            val patch = stack.mallocInt(1)

            FT_Library_Version(library, major, minor, patch)
            return LibraryVersion(major.get(0), minor.get(0), patch.get(0))
        }
    }

    override fun newMemoryFace(
        library: Long,
        data: NativeBinaryBuffer,
        length: Int,
        faceIndex: Long
    ): Long {
        val pp = MemoryUtil.memAllocPointer(1)
        println("Font signature: ${data.byteBuffer[0]}, ${data.byteBuffer[1]}, ${data.byteBuffer[2]}, ${data.byteBuffer[3]}")
        val err = FT_New_Memory_Face(
            library,
            data.byteBuffer,
            faceIndex,
            pp
        )
        check(err == FT_Err_Ok) { "Failed to create new face: " + FT_Error_String(err) }
        return pp.get(0).also { faceCache[it] = FT_Face.create(it) }
    }

    override fun loadMathTable(
        face: Long,
        data: NativeBinaryBuffer,
        length: Int
    ): Boolean {
        val face = getFaceFromCache(face)
        val lengthVar = MemoryUtil.memAllocCLong(1)
        lengthVar.put(0, 0)
        // 先查询 MATH 表的长度
        FT_Load_Sfnt_Table(face, TTAG_MATH.toLong(), 0, null, lengthVar)
        // 再加载 MATH 表
        val err = FT_Load_Sfnt_Table(face, TTAG_MATH.toLong(), 0, data.byteBuffer, lengthVar)
        return err == FT_Err_Ok
    }

    override fun faceGetAscender(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.ascender().toInt()
        }
    }

    override fun faceGetDescender(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.descender().toInt()
        }
    }

    override fun faceGetFaceFlags(face: Long): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.face_flags()
        }
    }

    override fun faceGetFaceIndex(face: Long): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.face_index()
        }
    }

    override fun faceGetFamilyName(face: Long): String {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.family_name().toByteString().utf8()
        }
    }

    override fun faceGetHeight(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.height().toInt()
        }
    }

    override fun faceGetMaxAdvanceHeight(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.max_advance_height().toInt()
        }
    }

    override fun faceGetMaxAdvanceWidth(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.max_advance_width().toInt()
        }
    }

    override fun faceGetNumFaces(face: Long): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.num_faces()
        }
    }

    override fun faceGetNumGlyphs(face: Long): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.num_glyphs()
        }
    }

    override fun faceGetStyleFlags(face: Long): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.style_flags()
        }
    }

    override fun faceGetStyleName(face: Long): String {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.style_name().toByteString().utf8()
        }
    }

    override fun faceGetUnderlinePosition(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.underline_position().toInt()
        }
    }

    override fun faceGetUnderlineThickness(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.underline_thickness().toInt()
        }
    }

    override fun faceGetUnitsPerEM(face: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return faceObj.units_per_EM().toInt()
        }
    }

    override fun faceGetGlyph(face: Long): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val glyphSlot = faceObj.glyph()
            return glyphSlot?.address() ?: 0L
        }
    }

    override fun faceGetSize(face: Long): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val size = faceObj.size()
            return size?.address() ?: 0L
        }
    }

    override fun getTrackKerning(
        face: Long,
        pointSize: Int,
        degree: Int
    ): Long {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val kerning = stack.mallocCLong(1)
            FT_Get_Track_Kerning(
                faceObj,
                pointSize.toLong(),
                degree,
                kerning
            )
            return kerning.get(0)
        }
    }

    override fun getKerning(
        face: Long,
        left: Char,
        right: Char,
        mode: Int
    ): Kerning {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val vector = FT_Vector.create(stack.mallocPointer(1).get(0))
            FT_Get_Kerning(
                faceObj,
                left.code,
                right.code,
                mode,
                vector
            )
            return Kerning(vector.x(), vector.y())
        }
    }

    override fun doneFace(face: Long): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Done_Face(faceObj)
            if (err == FT_Err_Ok) {
                faceCache.remove(face)
                return true
            } else {
                return false
            }
        }
    }

    override fun referenceFace(face: Long): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Reference_Face(faceObj)
            return err == FT_Err_Ok
        }
    }

    override fun hasKerning(face: Long): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return FT_HAS_KERNING(faceObj)
        }
    }

    override fun getPostscriptName(face: Long): String {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return FT_Get_Postscript_Name(faceObj).orEmpty()
        }
    }

    override fun selectCharMap(face: Long, encoding: Int): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Select_Charmap(faceObj, encoding)
            return err == FT_Err_Ok
        }
    }

    override fun setCharMap(
        face: Long,
        charMap: CharMap
    ): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val charMapObj = FT_CharMap.create(charMap.pointer)
            val err = FT_Set_Charmap(faceObj, charMapObj)
            return err == FT_Err_Ok
        }
    }

    override fun faceCheckTrueTypePatents(face: Long): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return FT_Face_CheckTrueTypePatents(faceObj)
        }
    }

    override fun faceSetUnpatentedHinting(
        face: Long,
        value: Boolean
    ): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return FT_Face_SetUnpatentedHinting(faceObj, value)
        }
    }

    override fun getFirstChar(face: Long): LongArray {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val intBuffer = stack.mallocInt(2)
            FT_Get_First_Char(faceObj, intBuffer)
            return longArrayOf(intBuffer.get(0).toLong(), intBuffer.get(0).toLong())
        }
    }

    override fun getNextChar(face: Long, charcode: Long): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val agIndex = stack.mallocInt(1)
            FT_Get_Next_Char(faceObj, charcode, agIndex)
            return agIndex.get(0)
        }
    }

    override fun getCharIndex(face: Long, code: Int): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return FT_Get_Char_Index(faceObj, code.toLong())
        }
    }

    override fun getNameIndex(face: Long, name: String): Int {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val nameBuffer = stack.UTF8(name)
            return FT_Get_Name_Index(faceObj, nameBuffer)
        }
    }

    override fun getGlyphName(face: Long, glyphIndex: Int): String {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val nameBuffer = stack.malloc(100) // Allocate buffer for glyph name
            val err = FT_Get_Glyph_Name(faceObj, glyphIndex, nameBuffer)
            if (err != FT_Err_Ok) {
                throw RuntimeException("Failed to get glyph name: " + FT_Error_String(err))
            }
            return nameBuffer.toByteString().utf8()
        }
    }

    override fun getFSTypeFlags(face: Long): Short {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            return FT_Get_FSType_Flags(faceObj)
        }
    }

    override fun selectSize(face: Long, strikeIndex: Int): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Select_Size(faceObj, strikeIndex)
            return err == FT_Err_Ok
        }
    }

    override fun loadChar(face: Long, c: Char, flags: Int): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Load_Char(faceObj, c.code.toLong(), flags)
            return err == FT_Err_Ok
        }
    }

    override fun requestSize(
        face: Long,
        sizeRequest: SizeRequest
    ): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val sizeRequestObj = FT_Size_Request.create(stack.mallocPointer(1).get(0))
            sizeRequestObj.type(sizeRequest.getType()?.ordinal ?: 0)
            sizeRequestObj.width(sizeRequest.width.toLong())
            sizeRequestObj.height(sizeRequest.height.toLong())
            sizeRequestObj.horiResolution(sizeRequest.horiResolution)
            sizeRequestObj.vertResolution(sizeRequest.vertResolution)
            val err = FT_Request_Size(faceObj, sizeRequestObj)
            return err == FT_Err_Ok
        }
    }

    override fun setPixelSizes(
        face: Long,
        width: Int,
        height: Int
    ): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Set_Pixel_Sizes(faceObj, width, height)
            return err == FT_Err_Ok
        }
    }

    override fun loadGlyph(
        face: Long,
        glyphIndex: Int,
        loadFlags: Int
    ): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Load_Glyph(faceObj, glyphIndex, loadFlags)
            return err == FT_Err_Ok
        }
    }

    override fun setCharSize(
        face: Long,
        charWidth: Int,
        charHeight: Int,
        horizResolution: Int,
        vertResolution: Int
    ): Boolean {
        stackPush().use { stack ->
            val faceObj = getFaceFromCache(face)
            val err = FT_Set_Char_Size(
                faceObj,
                charWidth.toLong(),
                charHeight.toLong(),
                horizResolution,
                vertResolution
            )
            return err == FT_Err_Ok
        }
    }

    override fun sizeGetMetrics(size: Long): Long {
        stackPush().use { stack ->
            val sizeObj = FT_Size.create(size)
            val metrics = sizeObj.metrics()
            return metrics.address()
        }
    }

    override fun sizeMetricsGetAscender(sizeMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.ascender()
        }
    }

    override fun sizeMetricsGetDescender(sizeMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.descender()
        }
    }

    override fun sizeMetricsGetHeight(sizeMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.height()
        }
    }

    override fun sizeMetricsGetMaxAdvance(sizeMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.max_advance()
        }
    }

    override fun sizeMetricsGetXPPEM(sizeMetrics: Long): Int {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.x_ppem().toInt()
        }
    }

    override fun sizeMetricsGetXScale(sizeMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.x_scale()
        }
    }

    override fun sizeMetricsGetYPPEM(sizeMetrics: Long): Int {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.y_ppem().toInt()
        }
    }

    override fun sizeMetricsGetYScale(sizeMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Size_Metrics.create(sizeMetrics)
            return metrics.y_scale()
        }
    }

    override fun glyphSlotGetLinearHoriAdvance(glyphSlot: Long): Long {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            return glyphSlotObj.linearHoriAdvance()
        }
    }

    override fun glyphSlotGetLinearVertAdvance(glyphSlot: Long): Long {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            return glyphSlotObj.linearVertAdvance()
        }
    }

    override fun glyphSlotGetAdvance(glyphSlot: Long): LongArray {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            val advance = glyphSlotObj.advance()
            return longArrayOf(advance.x(), advance.y())
        }
    }

    override fun glyphSlotGetFormat(glyphSlot: Long): Int {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            return glyphSlotObj.format()
        }
    }

    override fun glyphSlotGetBitmapLeft(glyphSlot: Long): Int {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            return glyphSlotObj.bitmap_left()
        }
    }

    override fun glyphSlotGetBitmapTop(glyphSlot: Long): Int {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            return glyphSlotObj.bitmap_top()
        }
    }

    override fun glyphSlotGetBitmap(glyphSlot: Long): Long {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            val bitmap = glyphSlotObj.bitmap()
            return bitmap.address()
        }
    }

    override fun glyphSlotGetMetrics(glyphSlot: Long): Long {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            val metrics = glyphSlotObj.metrics()
            return metrics.address()
        }
    }

    override fun renderGlyph(glyphSlot: Long, renderMode: Int): Boolean {
        stackPush().use { stack ->
            val glyphSlotObj = FT_GlyphSlot.create(glyphSlot)
            val err = FT_Render_Glyph(glyphSlotObj, renderMode)
            return err == FT_Err_Ok
        }
    }

    override fun glyphMetricsGetWidth(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.width()
        }
    }

    override fun glyphMetricsGetHeight(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.height()
        }
    }

    override fun glyphMetricsGetHoriAdvance(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.horiAdvance()
        }
    }

    override fun glyphMetricsGetVertAdvance(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.vertAdvance()
        }
    }

    override fun glyphMetricsGetHoriBearingX(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.horiBearingX()
        }
    }

    override fun glyphMetricsGetHoriBearingY(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.horiBearingY()
        }
    }

    override fun glyphMetricsGetVertBearingX(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.vertBearingX()
        }
    }

    override fun glyphMetricsGetVertBearingY(glyphMetrics: Long): Long {
        stackPush().use { stack ->
            val metrics = FT_Glyph_Metrics.create(glyphMetrics)
            return metrics.vertBearingY()
        }
    }

    override fun bitmapGetWidth(bitmap: Long): Int {
        stackPush().use { stack ->
            val bitmapObj = FT_Bitmap.create(bitmap)
            return bitmapObj.width()
        }
    }

    override fun bitmapGetRows(bitmap: Long): Int {
        stackPush().use { stack ->
            val bitmapObj = FT_Bitmap.create(bitmap)
            return bitmapObj.rows()
        }
    }

    override fun bitmapGetPitch(bitmap: Long): Int {
        stackPush().use { stack ->
            val bitmapObj = FT_Bitmap.create(bitmap)
            return bitmapObj.pitch()
        }
    }

    override fun bitmapGetNumGrays(bitmap: Long): Short {
        stackPush().use { stack ->
            val bitmapObj = FT_Bitmap.create(bitmap)
            return bitmapObj.num_grays()
        }
    }

    override fun bitmapGetPaletteMode(bitmap: Long): Char {
        stackPush().use { stack ->
            val bitmapObj = FT_Bitmap.create(bitmap)
            return bitmapObj.palette_mode().toInt().toChar()
        }
    }

    override fun bitmapGetPixelMode(bitmap: Long): Char {
        stackPush().use { stack ->
            val bitmapObj = FT_Bitmap.create(bitmap)
            return bitmapObj.pixel_mode().toInt().toChar()
        }
    }

    override fun bitmapGetBuffer(bitmap: Long): NativeBinaryBuffer {
        stackPush().use { stack ->
            val bitmapObj = FT_Bitmap.create(bitmap)
            val size = bitmapObj.rows() * bitmapObj.pitch().absoluteValue
            val buffer = bitmapObj.buffer(size)
            return NativeBinaryBuffer(buffer!!)
        }
    }

    override fun getCharMapIndex(charMap: Long): Int {
        stackPush().use { stack ->
            val charMapObj = FT_CharMap.create(charMap)
            return FT_Get_Charmap_Index(charMapObj)
        }
    }

    override fun newBuffer(size: Int): NativeBinaryBuffer {
        val buffer = MemoryUtil.memAlloc(size)
        buffer.order(ByteOrder.BIG_ENDIAN)
        return NativeBinaryBuffer(buffer)
    }
}
