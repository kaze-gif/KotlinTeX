package io.github.darriousliu.katex.freetype

internal class SizeRequest(
    type: FreeTypeConstants.FT_Size_Request_Type,
    val width: Int,
    val height: Int,
    val horiResolution: Int,
    val vertResolution: Int
) {
    private var type: Int

    init {
        this.type = type.ordinal
    }

    fun getType(): FreeTypeConstants.FT_Size_Request_Type? {
        return FreeTypeConstants.FT_Size_Request_Type.entries[type]
    }

    fun setType(type: FreeTypeConstants.FT_Size_Request_Type) {
        this.type = type.ordinal
    }
}