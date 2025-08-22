package io.github.darriousliu.katex.freetype

class Size(pointer: Long) : Pointer(pointer) {
    val metrics: SizeMetrics?
        get() {
            val sizeMetrics = FreeType.sizeGetMetrics(pointer)
            if (sizeMetrics <= 0) return null
            return SizeMetrics(sizeMetrics)
        }
}