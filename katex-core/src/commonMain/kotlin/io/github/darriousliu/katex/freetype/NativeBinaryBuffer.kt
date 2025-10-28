package io.github.darriousliu.katex.freetype

internal expect class NativeBinaryBuffer {
    val size: Int

    val short: Short
    val int: Int
    fun position(): Int
    fun position(newPosition: Int)
    fun limit(newLimit: Int)
    fun remaining(): Int

    fun fill(bytes: ByteArray)
    fun toByteArray(): ByteArray
    fun free()   // 释放内存
}