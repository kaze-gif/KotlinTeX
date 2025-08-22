@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.darriousliu.katex.freetype

import java.nio.ByteBuffer

object FreeTypeAndroid : IFreeType {
    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        try {
            System.loadLibrary("freetypejni")
            /*
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				int bits = 86;
				if (System.getProperty("os.arch").contains("64"))
					bits = 64;
				System.loadLibrary("freetype26MT_x" + bits);
			} else
				throw new Exception("Operating system not supported.");
				*/
        } catch (e: UnsatisfiedLinkError) {
            System.err.println("Can't find the native file for FreeType-jni.")
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    external override fun init(): Long

    external override fun doneFreeType(library: Long): Boolean

    external override fun libraryVersion(library: Long): LibraryVersion

    override fun newMemoryFace(
        library: Long,
        data: NativeBinaryBuffer,
        length: Int,
        faceIndex: Long
    ): Long {
        return newMemoryFace(library, data.byteBuffer, length, faceIndex)
    }

    external fun newMemoryFace(
        library: Long,
        data: ByteBuffer,
        length: Int,
        faceIndex: Long
    ): Long

    override fun loadMathTable(face: Long, data: NativeBinaryBuffer, length: Int): Boolean {
        // 先查询 MATH 表的长度
        val mathTableLength = getMathTableLength(face)
        return loadMathTable(face, data.byteBuffer, mathTableLength)
    }

    external fun getMathTableLength(face: Long): Long

    external fun loadMathTable(face: Long, data: ByteBuffer, length: Long): Boolean

    external override fun faceGetAscender(face: Long): Int

    external override fun faceGetDescender(face: Long): Int

    external override fun faceGetFaceFlags(face: Long): Long

    external override fun faceGetFaceIndex(face: Long): Long

    external override fun faceGetFamilyName(face: Long): String

    external override fun faceGetHeight(face: Long): Int

    external override fun faceGetMaxAdvanceHeight(face: Long): Int

    external override fun faceGetMaxAdvanceWidth(face: Long): Int

    external override fun faceGetNumFaces(face: Long): Long

    external override fun faceGetNumGlyphs(face: Long): Long

    external override fun faceGetStyleFlags(face: Long): Long

    external override fun faceGetStyleName(face: Long): String

    external override fun faceGetUnderlinePosition(face: Long): Int

    external override fun faceGetUnderlineThickness(face: Long): Int

    external override fun faceGetUnitsPerEM(face: Long): Int

    external override fun faceGetGlyph(face: Long): Long

    external override fun faceGetSize(face: Long): Long

    external override fun getTrackKerning(
        face: Long,
        pointSize: Int,
        degree: Int
    ): Long

    external override fun getKerning(
        face: Long,
        left: Char,
        right: Char,
        mode: Int
    ): Kerning

    external override fun doneFace(face: Long): Boolean

    external override fun referenceFace(face: Long): Boolean

    external override fun hasKerning(face: Long): Boolean

    external override fun getPostscriptName(face: Long): String

    external override fun selectCharMap(face: Long, encoding: Int): Boolean

    override fun setCharMap(
        face: Long,
        charMap: CharMap
    ): Boolean {
        return setCharMap(face, charMap.pointer)
    }

    external fun setCharMap(
        face: Long,
        charMap: Long
    ): Boolean

    external override fun faceCheckTrueTypePatents(face: Long): Boolean

    external override fun faceSetUnpatentedHinting(
        face: Long,
        value: Boolean
    ): Boolean

    external override fun getFirstChar(face: Long): LongArray

    external override fun getNextChar(face: Long, charcode: Long): Int

    external override fun getCharIndex(face: Long, code: Int): Int

    external override fun getNameIndex(face: Long, name: String): Int

    external override fun getGlyphName(face: Long, glyphIndex: Int): String

    external override fun getFSTypeFlags(face: Long): Short

    external override fun selectSize(face: Long, strikeIndex: Int): Boolean

    external override fun loadChar(face: Long, c: Char, flags: Int): Boolean

    external override fun requestSize(
        face: Long,
        sizeRequest: SizeRequest
    ): Boolean

    external override fun setPixelSizes(
        face: Long,
        width: Int,
        height: Int
    ): Boolean

    external override fun loadGlyph(
        face: Long,
        glyphIndex: Int,
        loadFlags: Int
    ): Boolean

    external override fun setCharSize(
        face: Long,
        charWidth: Int,
        charHeight: Int,
        horizResolution: Int,
        vertResolution: Int
    ): Boolean

    external override fun sizeGetMetrics(size: Long): Long

    external override fun sizeMetricsGetAscender(sizeMetrics: Long): Long

    external override fun sizeMetricsGetDescender(sizeMetrics: Long): Long

    external override fun sizeMetricsGetHeight(sizeMetrics: Long): Long

    external override fun sizeMetricsGetMaxAdvance(sizeMetrics: Long): Long

    external override fun sizeMetricsGetXPPEM(sizeMetrics: Long): Int

    external override fun sizeMetricsGetXScale(sizeMetrics: Long): Long

    external override fun sizeMetricsGetYPPEM(sizeMetrics: Long): Int

    external override fun sizeMetricsGetYScale(sizeMetrics: Long): Long

    external override fun glyphSlotGetLinearHoriAdvance(glyphSlot: Long): Long

    external override fun glyphSlotGetLinearVertAdvance(glyphSlot: Long): Long

    external override fun glyphSlotGetAdvance(glyphSlot: Long): LongArray

    external override fun glyphSlotGetFormat(glyphSlot: Long): Int

    external override fun glyphSlotGetBitmapLeft(glyphSlot: Long): Int

    external override fun glyphSlotGetBitmapTop(glyphSlot: Long): Int

    external override fun glyphSlotGetBitmap(glyphSlot: Long): Long

    external override fun glyphSlotGetMetrics(glyphSlot: Long): Long

    external override fun renderGlyph(glyphSlot: Long, renderMode: Int): Boolean

    external override fun glyphMetricsGetWidth(glyphMetrics: Long): Long

    external override fun glyphMetricsGetHeight(glyphMetrics: Long): Long

    external override fun glyphMetricsGetHoriAdvance(glyphMetrics: Long): Long

    external override fun glyphMetricsGetVertAdvance(glyphMetrics: Long): Long

    external override fun glyphMetricsGetHoriBearingX(glyphMetrics: Long): Long

    external override fun glyphMetricsGetHoriBearingY(glyphMetrics: Long): Long

    external override fun glyphMetricsGetVertBearingX(glyphMetrics: Long): Long

    external override fun glyphMetricsGetVertBearingY(glyphMetrics: Long): Long

    external override fun bitmapGetWidth(bitmap: Long): Int

    external override fun bitmapGetRows(bitmap: Long): Int

    external override fun bitmapGetPitch(bitmap: Long): Int

    external override fun bitmapGetNumGrays(bitmap: Long): Short

    external override fun bitmapGetPaletteMode(bitmap: Long): Char

    external override fun bitmapGetPixelMode(bitmap: Long): Char

    override fun bitmapGetBuffer(bitmap: Long): NativeBinaryBuffer {
        val byteBuffer = nativeBitmapGetBuffer(bitmap)
        val buffer = NativeBinaryBuffer(byteBuffer)
        return buffer
    }

    external fun nativeBitmapGetBuffer(bitmap: Long): ByteBuffer

    external override fun getCharMapIndex(charMap: Long): Int

    override fun newBuffer(size: Int): NativeBinaryBuffer {
        val byteBuffer = newNativeBuffer(size)
        return NativeBinaryBuffer(byteBuffer)
    }

    external fun newNativeBuffer(size: Int): ByteBuffer

    external fun deleteNativeBuffer(byteBuffer: ByteBuffer)
}