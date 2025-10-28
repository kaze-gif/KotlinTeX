package io.github.darriousliu.katex.utils

internal fun String.codePointAt(index: Int): Int {
    val ch1 = this[index]
    if (ch1.isHighSurrogate() && index + 1 < length) {
        val ch2 = this[index + 1]
        if (ch2.isLowSurrogate()) {
            return ((ch1.code - 0xD800) shl 10) + (ch2.code - 0xDC00) + 0x10000
        }
    }
    return ch1.code
}

internal fun Int.codePointToChars(): CharArray {
    return when (this) {
        in 0..0xFFFF -> {
            charArrayOf(toChar())
        }

        in 0x10000..0x10FFFF -> {
            val cpPrime = this - 0x10000
            val high = 0xD800 + (cpPrime shr 10)
            val low = 0xDC00 + (cpPrime and 0x3FF)
            charArrayOf(high.toChar(), low.toChar())
        }

        else -> {
            throw IllegalArgumentException("Invalid Unicode code point: $this")
        }
    }
}

/**
 * 统计字符串在 UTF-16 单元索引 [beginIndex, endIndex) 区间内的 Unicode code point 数量。
 */
internal fun String.codePointCount(beginIndex: Int = 0, endIndex: Int = length): Int {
    require(beginIndex in 0..length) { "beginIndex out of range" }
    require(endIndex in beginIndex..length) { "endIndex out of range" }

    var count = 0
    var i = beginIndex
    while (i < endIndex) {
        val ch = this[i]
        i += if (ch.isHighSurrogate() && i + 1 < endIndex && this[i + 1].isLowSurrogate()) {
            // 发现一个代理对，算作一个 code point
            2
        } else {
            1
        }
        count += 1
    }
    return count
}

internal fun Int.charCount(): Int =
    when (this) {
        in 0..0xFFFF -> 1
        in 0x10000..0x10FFFF -> 2
        else -> throw IllegalArgumentException("Invalid Unicode code point: $this")
    }

// 保留n位小数
internal fun Float.toFixed(n: Int): String {
    val str = this.toString()
    val dotIndex = str.indexOf('.')
    return if (dotIndex == -1) {
        str + "." + "0".repeat(n)
    } else {
        val decimalPart = str.substring(dotIndex + 1)
        if (decimalPart.length >= n) {
            str.substring(0, dotIndex + 1) + decimalPart.substring(0, n)
        } else {
            str + "0".repeat(n - decimalPart.length)
        }
    }
}

internal expect fun readAssetFile(path: String): ByteArray
