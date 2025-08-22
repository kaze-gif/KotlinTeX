@file:OptIn(ExperimentalForeignApi::class)

package io.github.darriousliu.katex.freetype

import freetype.FT_Face
import freetype.TTAG_MATH
import kotlinx.cinterop.*
import platform.posix.free

object FreeTypeIos : IFreeType {
    override fun init(): Long {
        return freetype.initLibrary()
    }

    override fun doneFreeType(library: Long): Boolean {
        return freetype.doneFreeType(library)
    }

    override fun libraryVersion(library: Long): LibraryVersion {
        val intArray = freetype.libraryVersion(library)
        return if (intArray != null) {
            LibraryVersion(intArray[0], intArray[1], intArray[2])
        } else {
            LibraryVersion(0, 0, 0)
        }.also {
            free(intArray)
        }
    }

    override fun newMemoryFace(
        library: Long,
        data: NativeBinaryBuffer,
        length: Int,
        faceIndex: Long
    ): Long {
        require(data.ptr != null) { "data.ptr is null" }
        return freetype.newMemoryFace(library, data.ptr!!, length, faceIndex)
    }

    override fun loadMathTable(face: Long, data: NativeBinaryBuffer, length: Int): Boolean {
        require(data.ptr != null) { "data.ptr is null" }
        return memScoped {
            val lengthVar = alloc<ULongVar>()
            val faceCPointer: FT_Face? = face.toCPointer()
            val tag = TTAG_MATH.toULong()
            // 先查询 MATH 表的长度
            freetype.FT_Load_Sfnt_Table(faceCPointer, tag, 0, null, lengthVar.ptr)
            // 再加载 MATH 表
            val result = freetype.FT_Load_Sfnt_Table(
                faceCPointer,
                tag,
                0,
                data.ptr?.reinterpret(),
                lengthVar.ptr
            )
            result == freetype.FT_Err_Ok.toInt()
        }
    }

    override fun faceGetAscender(face: Long): Int {
        return freetype.faceGetAscender(face)
    }

    override fun faceGetDescender(face: Long): Int {
        return freetype.faceGetDescender(face)
    }

    override fun faceGetFaceFlags(face: Long): Long {
        return freetype.faceGetFaceFlags(face)
    }

    override fun faceGetFaceIndex(face: Long): Long {
        return freetype.faceGetFaceIndex(face)
    }

    override fun faceGetFamilyName(face: Long): String {
        return freetype.faceGetFamilyName(face)?.toKString().orEmpty()
    }

    override fun faceGetHeight(face: Long): Int {
        return freetype.faceGetHeight(face)
    }

    override fun faceGetMaxAdvanceHeight(face: Long): Int {
        return freetype.faceGetMaxAdvanceHeight(face)
    }

    override fun faceGetMaxAdvanceWidth(face: Long): Int {
        return freetype.faceGetMaxAdvanceWidth(face)
    }

    override fun faceGetNumFaces(face: Long): Long {
        return freetype.faceGetNumFaces(face)
    }

    override fun faceGetNumGlyphs(face: Long): Long {
        return freetype.faceGetNumGlyphs(face)
    }

    override fun faceGetStyleFlags(face: Long): Long {
        return freetype.faceGetStyleFlags(face)
    }

    override fun faceGetStyleName(face: Long): String {
        return freetype.faceGetStyleName(face)?.toKString().orEmpty()
    }

    override fun faceGetUnderlinePosition(face: Long): Int {
        return freetype.faceGetUnderlinePosition(face)
    }

    override fun faceGetUnderlineThickness(face: Long): Int {
        return freetype.faceGetUnderlineThickness(face)
    }

    override fun faceGetUnitsPerEM(face: Long): Int {
        return freetype.faceGetUnitsPerEM(face)
    }

    override fun faceGetGlyph(face: Long): Long {
        return freetype.faceGetGlyph(face)
    }

    override fun faceGetSize(face: Long): Long {
        return freetype.faceGetSize(face)
    }

    override fun getTrackKerning(
        face: Long,
        pointSize: Int,
        degree: Int
    ): Long {
        return freetype.getTrackKerning(face, pointSize, degree)
    }

    override fun getKerning(
        face: Long,
        left: Char,
        right: Char,
        mode: Int
    ): Kerning {
        val longArray = freetype.getKerning(face, left.code, right.code, mode)
        return if (longArray != null) {
            Kerning(longArray[0], longArray[1])
        } else {
            Kerning(0, 0)
        }.also {
            free(longArray)
        }
    }

    override fun doneFace(face: Long): Boolean {
        return freetype.doneFace(face)
    }

    override fun referenceFace(face: Long): Boolean {
        return freetype.referenceFace(face)
    }

    override fun hasKerning(face: Long): Boolean {
        return freetype.hasKerning(face)
    }

    override fun getPostscriptName(face: Long): String {
        return freetype.FT_Get_Postscript_Name(face.toCPointer())?.toKString().orEmpty()
    }

    override fun selectCharMap(face: Long, encoding: Int): Boolean {
        return freetype.selectCharMap(face, encoding)
    }

    override fun setCharMap(
        face: Long,
        charMap: CharMap
    ): Boolean {
        return freetype.setCharMap(face, charMap.pointer)
    }

    override fun faceCheckTrueTypePatents(face: Long): Boolean {
        return freetype.faceCheckTrueTypePatents(face)
    }

    override fun faceSetUnpatentedHinting(
        face: Long,
        value: Boolean
    ): Boolean {
        return freetype.faceSetUnpatentedHinting(face, value)
    }

    override fun getFirstChar(face: Long): LongArray {
        val uLongArray = freetype.getFirstChar(face)
        return if (uLongArray != null) {
            longArrayOf(uLongArray[0].toLong(), uLongArray[1].toLong())
        } else {
            longArrayOf()
        }.also {
            free(uLongArray)
        }
    }

    override fun getNextChar(face: Long, charcode: Long): Int {
        return freetype.getNextChar(face, charcode).toInt()
    }

    override fun getCharIndex(face: Long, code: Int): Int {
        return freetype.getCharIndex(face, code).toInt()
    }

    override fun getNameIndex(face: Long, name: String): Int {
        return freetype.getNameIndex(face, name).toInt()
    }

    override fun getGlyphName(face: Long, glyphIndex: Int): String {
        memScoped {
            val nameBuffer = allocArray<ByteVar>(100) // 分配100字节缓冲区
            // 调用C函数
            freetype.FT_Get_Glyph_Name(face.toCPointer(), glyphIndex.toUInt(), nameBuffer, 100u)
            // 转换为Kotlin字符串
            return nameBuffer.toKString()
        }
    }

    override fun getFSTypeFlags(face: Long): Short {
        return freetype.getFSTypeFlags(face).toShort()
    }

    override fun selectSize(face: Long, strikeIndex: Int): Boolean {
        return freetype.selectSize(face, strikeIndex)
    }

    override fun loadChar(face: Long, c: Char, flags: Int): Boolean {
        return freetype.loadChar(face, c.code, flags)
    }

    override fun requestSize(
        face: Long,
        sizeRequest: SizeRequest
    ): Boolean {
        return freetype.requestSize(
            face = face,
            width = sizeRequest.width,
            height = sizeRequest.height,
            horizResolution = sizeRequest.horiResolution,
            vertResolution = sizeRequest.vertResolution,
            type = sizeRequest.getType()?.ordinal ?: 0
        )
    }

    override fun setPixelSizes(
        face: Long,
        width: Int,
        height: Int
    ): Boolean {
        return freetype.setPixelSizes(face, width, height)
    }

    override fun loadGlyph(
        face: Long,
        glyphIndex: Int,
        loadFlags: Int
    ): Boolean {
        return freetype.loadGlyph(face, glyphIndex, loadFlags)
    }

    override fun setCharSize(
        face: Long,
        charWidth: Int,
        charHeight: Int,
        horizResolution: Int,
        vertResolution: Int
    ): Boolean {
        return freetype.setCharSize(face, charWidth, charHeight, horizResolution, vertResolution)
    }

    override fun sizeGetMetrics(size: Long): Long {
        return freetype.sizeGetMetrics(size)
    }

    override fun sizeMetricsGetAscender(sizeMetrics: Long): Long {
        return freetype.sizeMetricsGetAscender(sizeMetrics)
    }

    override fun sizeMetricsGetDescender(sizeMetrics: Long): Long {
        return freetype.sizeMetricsGetDescender(sizeMetrics)
    }

    override fun sizeMetricsGetHeight(sizeMetrics: Long): Long {
        return freetype.sizeMetricsGetHeight(sizeMetrics)
    }

    override fun sizeMetricsGetMaxAdvance(sizeMetrics: Long): Long {
        return freetype.sizeMetricsGetMaxAdvance(sizeMetrics)
    }

    override fun sizeMetricsGetXPPEM(sizeMetrics: Long): Int {
        return freetype.sizeMetricsGetXPPEM(sizeMetrics)
    }

    override fun sizeMetricsGetXScale(sizeMetrics: Long): Long {
        return freetype.sizeMetricsGetXScale(sizeMetrics)
    }

    override fun sizeMetricsGetYPPEM(sizeMetrics: Long): Int {
        return freetype.sizeMetricsGetYPPEM(sizeMetrics)
    }

    override fun sizeMetricsGetYScale(sizeMetrics: Long): Long {
        return freetype.sizeMetricsGetYScale(sizeMetrics)
    }

    override fun glyphSlotGetLinearHoriAdvance(glyphSlot: Long): Long {
        return freetype.glyphSlotGetLinearHoriAdvance(glyphSlot)
    }

    override fun glyphSlotGetLinearVertAdvance(glyphSlot: Long): Long {
        return freetype.glyphSlotGetLinearVertAdvance(glyphSlot)
    }

    override fun glyphSlotGetAdvance(glyphSlot: Long): LongArray {
        val longArray = freetype.glyphSlotGetAdvance(glyphSlot)
        return if (longArray != null) {
            longArrayOf(longArray[0], longArray[1])
        } else {
            longArrayOf(0, 0)
        }.also {
            free(longArray)
        }
    }

    override fun glyphSlotGetFormat(glyphSlot: Long): Int {
        return freetype.glyphSlotGetFormat(glyphSlot)
    }

    override fun glyphSlotGetBitmapLeft(glyphSlot: Long): Int {
        return freetype.glyphSlotGetBitmapLeft(glyphSlot)
    }

    override fun glyphSlotGetBitmapTop(glyphSlot: Long): Int {
        return freetype.glyphSlotGetBitmapTop(glyphSlot)
    }

    override fun glyphSlotGetBitmap(glyphSlot: Long): Long {
        return freetype.glyphSlotGetBitmap(glyphSlot)
    }

    override fun glyphSlotGetMetrics(glyphSlot: Long): Long {
        return freetype.glyphSlotGetMetrics(glyphSlot)
    }

    override fun renderGlyph(glyphSlot: Long, renderMode: Int): Boolean {
        return freetype.renderGlyph(glyphSlot, renderMode)
    }

    override fun glyphMetricsGetWidth(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetWidth(glyphMetrics)
    }

    override fun glyphMetricsGetHeight(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetHeight(glyphMetrics)
    }

    override fun glyphMetricsGetHoriAdvance(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetHoriAdvance(glyphMetrics)
    }

    override fun glyphMetricsGetVertAdvance(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetVertAdvance(glyphMetrics)
    }

    override fun glyphMetricsGetHoriBearingX(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetHoriBearingX(glyphMetrics)
    }

    override fun glyphMetricsGetHoriBearingY(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetHoriBearingY(glyphMetrics)
    }

    override fun glyphMetricsGetVertBearingX(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetVertBearingX(glyphMetrics)
    }

    override fun glyphMetricsGetVertBearingY(glyphMetrics: Long): Long {
        return freetype.glyphMetricsGetVertBearingY(glyphMetrics)
    }

    override fun bitmapGetWidth(bitmap: Long): Int {
        return freetype.bitmapGetWidth(bitmap).toInt()
    }

    override fun bitmapGetRows(bitmap: Long): Int {
        return freetype.bitmapGetRows(bitmap).toInt()
    }

    override fun bitmapGetPitch(bitmap: Long): Int {
        return freetype.bitmapGetPitch(bitmap)
    }

    override fun bitmapGetNumGrays(bitmap: Long): Short {
        return freetype.bitmapGetNumGrays(bitmap).toShort()
    }

    override fun bitmapGetPaletteMode(bitmap: Long): Char {

        return Char(freetype.bitmapGetPaletteMode(bitmap).toInt())
    }

    override fun bitmapGetPixelMode(bitmap: Long): Char {
        return Char(freetype.bitmapGetPixelMode(bitmap).toInt())
    }

    override fun bitmapGetBuffer(bitmap: Long): NativeBinaryBuffer {
        val array = freetype.bitmapGetBuffer(bitmap)
        return array.useContents {
            NativeBinaryBuffer(length, ptr)
        }
    }

    override fun getCharMapIndex(charMap: Long): Int {
        return freetype.getCharMapIndex(charMap)
    }

    override fun newBuffer(size: Int): NativeBinaryBuffer {
        return NativeBinaryBuffer(size)
    }
}
