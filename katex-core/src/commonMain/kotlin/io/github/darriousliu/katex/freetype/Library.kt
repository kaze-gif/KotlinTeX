package io.github.darriousliu.katex.freetype

import io.github.darriousliu.katex.utils.readAssetFile

/**
 * Each library is completely independent from the others; it is the root of a set of objects like fonts, faces, sizes, etc.
 */
internal class Library(pointer: Long) : Pointer(pointer) {
    /**
     * Destroy the library object and all of it's childrens, including faces, sizes, etc.
     */
    fun delete(): Boolean {
        return FreeType.doneFreeType(pointer)
    }

    /**
     * Create a new Face object from file<br></br>
     * It will return null in case of error.
     */
    fun newFace(file: String, faceIndex: Int): Face? {
        try {
            val byteArray = readAssetFile(file)
            return newFace(byteArray, faceIndex)
        } catch (e: Exception) {
            println(e)
        }
        return null
    }

    /**
     * Create a new Face object from a byte[]<br></br>
     * It will return null in case of error.
     */
    fun newFace(file: ByteArray, faceIndex: Int): Face? {
        val buffer = FreeType.newBuffer(file.size)
        buffer.limit(buffer.position() + file.size)
        FreeType.fillBuffer(file, buffer, file.size)
        return newFace(buffer, faceIndex)
    }

    /**
     * Create a new Face object from a ByteBuffer.<br></br>
     * It will return null in case of error.<br></br>
     * Take care that the ByteByffer must be a direct buffer created with Utils.newBuffer and filled with Utils.fillBuffer.
     */
    fun newFace(file: NativeBinaryBuffer, faceIndex: Int): Face? {
        val face = FreeType.newMemoryFace(pointer, file, file.remaining(), faceIndex.toLong())
        if (face == 0L) {
            FreeType.deleteBuffer(file)
            return null
        }
        return Face(face, file)
    }

    val version: LibraryVersion
        /**
         * Returns a LibraryVersion object containing the information about the version of FreeType
         */
        get() = FreeType.libraryVersion(pointer)
}