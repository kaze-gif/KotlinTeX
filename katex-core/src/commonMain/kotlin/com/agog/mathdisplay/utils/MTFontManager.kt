package com.agog.mathdisplay.utils

import com.agog.mathdisplay.render.MTFont


internal const val KDefaultFontSize = 20f

object MTFontManager {
    private val nameToFontMap = hashMapOf<String, MTFont>()

    /**
     * @param name  filename in that assets directory of the opentype font minus the otf extension
     * @param size  device pixels
     */
    fun fontWithName(name: String, size: Float): MTFont {
        var f = nameToFontMap[name]
        if (f == null) {
            f = MTFont(size, name)
            nameToFontMap[name] = f
            return f
        }
        return if (f.fontSize == size) {
            f
        } else {
            f.copyFontWithSize(size)
        }
    }

    fun latinModernFontWithSize(size: Float): MTFont {
        return fontWithName("latinmodern-math", size)
    }

    fun xitsFontWithSize(size: Float): MTFont {
        return fontWithName("xits-math", size)
    }

    fun termesFontWithSize(size: Float): MTFont {
        return fontWithName("texgyretermes-math", size)
    }

    fun defaultFont(): MTFont {
        return latinModernFontWithSize(KDefaultFontSize)
    }
}