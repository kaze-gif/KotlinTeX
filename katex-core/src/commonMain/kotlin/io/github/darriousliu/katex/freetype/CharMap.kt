package io.github.darriousliu.katex.freetype

class CharMap(pointer: Long) : Pointer(pointer) {
    companion object {
        fun getCharMapIndex(charmap: CharMap): Int {
            return FreeType.getCharMapIndex(charmap.pointer)
        }
    }
}