package io.github.darriousliu.katex.freetype

class Bitmap(pointer: Long) : Pointer(pointer) {
    val width: Int
        get() = FreeType.bitmapGetWidth(pointer)
    val rows: Int
        get() = FreeType.bitmapGetRows(pointer)
    val pitch: Int
        get() = FreeType.bitmapGetPitch(pointer)
    val numGrays: Short
        get() = FreeType.bitmapGetNumGrays(pointer)
    val paletteMode: Char
        get() = FreeType.bitmapGetPaletteMode(pointer)
    val pixelMode: Char
        get() = FreeType.bitmapGetPixelMode(pointer)
    val buffer: NativeBinaryBuffer
        get() = FreeType.bitmapGetBuffer(pointer)
}