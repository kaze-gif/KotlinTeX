package com.pvporbit.freetype

import android.content.Context
import io.github.darriousliu.katex.core.resources.Res

internal lateinit var appContext: Context

internal actual fun readAssetFile(path: String): ByteArray {
    return try {
        val realPath = Res.getUri(path)
        val relativePath = if (realPath.startsWith("file:///android_asset/")) {
            realPath.removePrefix("file:///android_asset/")
        } else {
            realPath
        }
        val assetManager = appContext.assets
        assetManager.open(relativePath).use { inputStream ->
            inputStream.readBytes()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        byteArrayOf()
    }
}