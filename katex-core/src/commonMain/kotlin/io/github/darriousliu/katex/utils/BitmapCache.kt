package io.github.darriousliu.katex.utils

import androidx.collection.lruCache
import androidx.compose.ui.graphics.ImageBitmap

object BitmapCache {
    private val cache = lruCache<String, ImageBitmap>(100)

    operator fun get(key: String): ImageBitmap? {
        return cache[key]
    }

    operator fun set(key: String, value: ImageBitmap) {
        cache.put(key, value)
    }

    fun resize(newSize: Int) {
        cache.resize(newSize)
    }

    fun clear() {
        cache.evictAll()
    }
}