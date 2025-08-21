package com.agog.mathdisplay.render

object MTColor {
    const val BLACK = 0xFF000000.toInt()
    const val DARK_GRAY = 0xFF444444.toInt()
    const val GRAY = 0xFF888888.toInt()
    const val LIGHT_GRAY = 0xFFCCCCCC.toInt()
    const val WHITE = 0xFFFFFFFF.toInt()
    const val RED = 0xFFFF0000.toInt()
    const val GREEN = 0xFF00FF00.toInt()
    const val BLUE = 0xFF0000FF.toInt()
    const val YELLOW = 0xFFFFFF00.toInt()
    const val CYAN = 0xFF00FFFF.toInt()
    const val MAGENTA = 0xFFFF00FF.toInt()
    const val TRANSPARENT = 0

    // 支持常用英文色名
    private val namedColors by lazy {
        mapOf(
            "black" to BLACK,
            "white" to WHITE,
            "red" to RED,
            "green" to GREEN,
            "blue" to BLUE,
            "yellow" to YELLOW,
            "cyan" to CYAN,
            "magenta" to MAGENTA,
            "gray" to GRAY,
        )
    }

    /**
     * 通用字符串转Color
     * 支持 #RGB/#ARGB/#RRGGBB/#AARRGGBB，部分英文色名
     */
    fun parseColor(colorString: String?): Int {
        if (colorString == null) return TRANSPARENT
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

            namedColors.containsKey(str.lowercase()) -> namedColors[str.lowercase()]!!
            else -> throw IllegalArgumentException("Unknown color format: $colorString")
        }
        return colorInt
    }
}