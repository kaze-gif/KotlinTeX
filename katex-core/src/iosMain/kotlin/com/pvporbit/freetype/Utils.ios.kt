package com.pvporbit.freetype

import io.github.darriousliu.katex.core.resources.Res
import kotlinx.coroutines.runBlocking

internal actual fun readAssetFile(path: String): ByteArray {
    try {
        return runBlocking { Res.readBytes(path) }
    } catch (e: Exception) {
        println(e)
    }
    return byteArrayOf()
}