package com.agog.mathdisplay.render

import androidx.compose.ui.graphics.Color

object MTColor {
    // 支持常用英文色名
    private val namedColors by lazy {
        mapOf(
            "black" to Color.Black.value,
            "darkgray" to Color.DarkGray.value,
            "gray" to Color.Gray.value,
            "lightgray" to Color.LightGray.value,
            "white" to Color.White.value,
            "red" to Color.Red.value,
            "green" to Color.Green.value,
            "blue" to Color.Blue.value,
            "yellow" to Color.Yellow.value,
            "cyan" to Color.Cyan.value,
            "magenta" to Color.Magenta.value,
            "transparent" to Color.Transparent.value
        )
    }

    /**
     * 通用字符串转Color
     * 支持 #RGB/#ARGB/#RRGGBB/#AARRGGBB，部分英文色名
     */
    fun parseColor(colorString: String?): Color {
        if (colorString == null) return Color.Transparent
        val str = colorString.trim()

        val colorInt = when {
            str.startsWith("#") -> {
                // 去掉 #
                val hex = str.substring(1)
                when (hex.length) {
                    3 -> { // #RGB
                        val r = hex[0].toString().repeat(2)
                        val g = hex[1].toString().repeat(2)
                        val b = hex[2].toString().repeat(2)
                        0xFF000000.toInt() or
                                (r.toInt(16) shl 16) or
                                (g.toInt(16) shl 8) or
                                b.toInt(16)
                    }

                    4 -> { // #ARGB
                        val a = hex[0].toString().repeat(2)
                        val r = hex[1].toString().repeat(2)
                        val g = hex[2].toString().repeat(2)
                        val b = hex[3].toString().repeat(2)
                        (a.toInt(16) shl 24) or
                                (r.toInt(16) shl 16) or
                                (g.toInt(16) shl 8) or
                                b.toInt(16)
                    }

                    6 -> { // #RRGGBB
                        0xFF000000.toInt() or hex.toInt(16)
                    }

                    8 -> { // #AARRGGBB
                        hex.toLong(16).toInt()
                    }

                    else -> throw IllegalArgumentException("Unknown color format: $colorString")
                }
            }

            namedColors.containsKey(str.lowercase()) -> namedColors[str.lowercase()]!!.toInt()
            else -> throw IllegalArgumentException("Unknown color format: $colorString")
        }
        return Color(colorInt)
    }
}