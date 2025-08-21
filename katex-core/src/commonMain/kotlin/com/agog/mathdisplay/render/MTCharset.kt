package com.agog.mathdisplay.render

import com.agog.mathdisplay.parse.MTFontStyle
import com.agog.mathdisplay.parse.MathDisplayException
import com.pvporbit.freetype.Utils
import com.pvporbit.freetype.Utils.codePointCount

/**
 * 计算字符串中包含的 Unicode 字符数量，一个 Unicode 字符可能由一个或多个 UTF-16 单元组成。
 *
 * @return 此字符串中的 Unicode 字符数量。
 */
internal fun String.numberOfGlyphs(): Int {
    return codePointCount(0, length)
}

data class CGGlyph(
    var gid: Int = 0,
    var glyphAscent: Float = 0.0f,
    var glyphDescent: Float = 0.0f,
    var glyphWidth: Float = 0.0f
) {
    val isValid: Boolean
        get() = gid != 0
}

internal const val kMTUnicodeGreekLowerStart: Char = '\u03B1'
internal const val kMTUnicodeGreekLowerEnd = '\u03C9'
internal const val kMTUnicodeGreekCapitalStart = '\u0391'
internal const val kMTUnicodeGreekCapitalEnd = '\u03A9'

// Note this is not equivalent to ch.isLowerCase() delta is a test case
internal fun isLowerEn(ch: Char): Boolean {
    return (ch) >= 'a' && (ch) <= 'z'
}

internal fun isUpperEn(ch: Char): Boolean {
    return (ch) >= 'A' && (ch) <= 'Z'
}

internal fun isNumber(ch: Char): Boolean {
    return (ch) >= '0' && (ch) <= '9'
}

/**
 * 判断给定字符是否属于希腊字母的小写字母范围。
 *
 * @param ch 要检查的字符。
 * @return 如果字符是希腊字母的小写字母，则返回 true；否则返回 false。
 */
internal fun isLowerGreek(ch: Char): Boolean {
    return (ch) >= kMTUnicodeGreekLowerStart && (ch) <= kMTUnicodeGreekLowerEnd
}

/**
 * 检查是否为希腊大写字母字符。
 *
 * @param ch 要检查的字符。
 * @return 如果字符是希腊大写字母，则返回true；否则返回false。
 */
internal fun isCapitalGreek(ch: Char): Boolean {
    return (ch) >= kMTUnicodeGreekCapitalStart && (ch) <= kMTUnicodeGreekCapitalEnd
}


/**
 * 返回给定字符在希腊符号特定顺序中的位置。
 *
 * @param ch 要查找位置的字符。
 * @return 如果字符是特定的希腊符号之一，则返回其在数组中的索引；否则返回 -1。
 */
internal fun greekSymbolOrder(ch: Char): Int {
    // These greek symbols that always appear in unicode in this particular order after the alphabet
    // The symbols are epsilon, vartheta, varkappa, phi, varrho, varpi.
    val greekSymbols = arrayOf(0x03F5, 0x03D1, 0x03F0, 0x03D5, 0x03F1, 0x03D6)
    return greekSymbols.indexOf(ch.code)
}

/**
 * 判断给定字符是否为希腊符号。
 *
 * @param ch 要判断的字符。
 * @return 如果字符是某个特定的希腊符号，则返回 true；否则返回 false。
 */
internal fun isGreekSymbol(ch: Char): Boolean {
    return (greekSymbolOrder(ch) != -1)
}

internal class MTCodepointChar(val codepoint: Int) {
    fun toUnicodeString(): String {
        val cs = Utils.codePointToChars(codepoint)
        val sb = StringBuilder()
        sb.append(cs)
        val sbs = sb.toString()
        return sbs
    }
}


// mathit
internal const val kMTUnicodePlanksConstant = 0x210e
internal const val kMTUnicodeMathCapitalItalicStart = 0x1D434
internal const val kMTUnicodeMathLowerItalicStart = 0x1D44E
internal const val kMTUnicodeGreekCapitalItalicStart = 0x1D6E2
internal const val kMTUnicodeGreekLowerItalicStart = 0x1D6FC
internal const val kMTUnicodeGreekSymbolItalicStart = 0x1D716

internal fun getItalicized(ch: Char): MTCodepointChar {
    // Special cases for italics
    when {
        ch == 'h' -> {  // italic h (plank's constant)
            return MTCodepointChar(kMTUnicodePlanksConstant)
        }

        isUpperEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathCapitalItalicStart + (ch - 'A'))
        }

        isLowerEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathLowerItalicStart + (ch - 'a'))
        }

        isCapitalGreek(ch) -> {
            // Capital Greek characters
            return MTCodepointChar(kMTUnicodeGreekCapitalItalicStart + (ch - kMTUnicodeGreekCapitalStart))
        }

        isLowerGreek(ch) -> {
            // Greek characters
            return MTCodepointChar(kMTUnicodeGreekLowerItalicStart + (ch - kMTUnicodeGreekLowerStart))
        }

        isGreekSymbol(ch) -> {
            return MTCodepointChar(kMTUnicodeGreekSymbolItalicStart + greekSymbolOrder(ch))
        }
    }
    // Note there are no italicized numbers in unicode so we don't support italicizing numbers.
    return MTCodepointChar(ch.code)
}

// mathbf
internal const val kMTUnicodeMathCapitalBoldStart = 0x1D400
internal const val kMTUnicodeMathLowerBoldStart = 0x1D41A
internal const val kMTUnicodeGreekCapitalBoldStart = 0x1D6A8
internal const val kMTUnicodeGreekLowerBoldStart = 0x1D6C2
internal const val kMTUnicodeGreekSymbolBoldStart = 0x1D6DC
internal const val kMTUnicodeNumberBoldStart = 0x1D7CE

internal fun getBold(ch: Char): MTCodepointChar {
    when {
        isUpperEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathCapitalBoldStart + (ch - 'A'))
        }

        isLowerEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathLowerBoldStart + (ch - 'a'))
        }

        isCapitalGreek(ch) -> {
            // Capital Greek characters
            return MTCodepointChar(kMTUnicodeGreekCapitalBoldStart + (ch - kMTUnicodeGreekCapitalStart))
        }

        isLowerGreek(ch) -> {
            // Greek characters
            return MTCodepointChar(kMTUnicodeGreekLowerBoldStart + (ch - kMTUnicodeGreekLowerStart))
        }

        isGreekSymbol(ch) -> {
            return MTCodepointChar(kMTUnicodeGreekSymbolBoldStart + greekSymbolOrder(ch))
        }

        isNumber(ch) -> {
            return MTCodepointChar(kMTUnicodeNumberBoldStart + (ch - '0'))
        }
    }
    return MTCodepointChar(ch.code)
}

// mathbfit
internal const val kMTUnicodeMathCapitalBoldItalicStart = 0x1D468
internal const val kMTUnicodeMathLowerBoldItalicStart = 0x1D482
internal const val kMTUnicodeGreekCapitalBoldItalicStart = 0x1D71C
internal const val kMTUnicodeGreekLowerBoldItalicStart = 0x1D736
internal const val kMTUnicodeGreekSymbolBoldItalicStart = 0x1D750

internal fun getBoldItalic(ch: Char): MTCodepointChar {
    when {
        isUpperEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathCapitalBoldItalicStart + (ch - 'A'))
        }

        isLowerEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathLowerBoldItalicStart + (ch - 'a'))
        }

        isCapitalGreek(ch) -> {
            // Capital Greek characters
            return MTCodepointChar(kMTUnicodeGreekCapitalBoldItalicStart + (ch - kMTUnicodeGreekCapitalStart))
        }

        isLowerGreek(ch) -> {
            // Greek characters
            return MTCodepointChar(kMTUnicodeGreekLowerBoldItalicStart + (ch - kMTUnicodeGreekLowerStart))
        }

        isGreekSymbol(ch) -> {
            return MTCodepointChar(kMTUnicodeGreekSymbolBoldItalicStart + greekSymbolOrder(ch))
        }

        isNumber(ch) -> {
            // No bold italic for numbers so we just bold them.
            return getBold(ch)
        }
    }
    return MTCodepointChar(ch.code)
}

// LaTeX default
internal fun getDefaultStyle(ch: Char): MTCodepointChar {
    when {
        isLowerEn(ch) || isUpperEn(ch) || isLowerGreek(ch) || isGreekSymbol(ch) -> {
            return getItalicized(ch)
        }

        isNumber(ch) || isCapitalGreek(ch) -> {
            return MTCodepointChar(ch.code)
        }

        ch == '.' -> {
            // . is treated as a number in our code, but it doesn't change fonts.
            return MTCodepointChar(ch.code)
        }
    }
    throw MathDisplayException("Unknown character $ch for default style.")
}

internal const val kMTUnicodeMathCapitalScriptStart = 0x1D49C
// TODO(kostub): Unused in Latin Modern Math - if another font is used determine if
// this should be applicable.
// static const MTCodepointChar kMTUnicodeMathLowerScriptStart = 0x1D4B6;

// mathcal/mathscr (calligraphic or script)
internal fun getCalligraphicChar(ch: Char): MTCodepointChar {
    // Calligraphic has lots of exceptions:
    when (ch) {
        'B' ->
            return MTCodepointChar(0x212C)   // Script B (bernoulli)
        'E' ->
            return MTCodepointChar(0x2130)   // Script E (emf)
        'F' ->
            return MTCodepointChar(0x2131)   // Script F (fourier)
        'H' ->
            return MTCodepointChar(0x210B)   // Script H (hamiltonian)
        'I' ->
            return MTCodepointChar(0x2110)   // Script I
        'L' ->
            return MTCodepointChar(0x2112)   // Script L (laplace)
        'M' ->
            return MTCodepointChar(0x2133)   // Script M (M-matrix)
        'R' ->
            return MTCodepointChar(0x211B)   // Script R (Riemann integral)
        'e' ->
            return MTCodepointChar(0x212F)   // Script e (Natural exponent)
        'g' ->
            return MTCodepointChar(0x210A)   // Script g (real number)
        'o' ->
            return MTCodepointChar(0x2134)   // Script o (order)
    }
    when {
        isUpperEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathCapitalScriptStart + (ch - 'A'))
        }

        isLowerEn(ch) -> {
            // Latin Modern Math does not have lower case calligraphic characters, so we use
            // the default style instead of showing a ?
            return getDefaultStyle(ch)
        }
    }
    // Calligraphic characters don't exist for greek or numbers, we give them the
    // default treatment.
    return getDefaultStyle(ch)
}

internal const val kMTUnicodeMathCapitalTTStart = 0x1D670
internal const val kMTUnicodeMathLowerTTStart = 0x1D68A
internal const val kMTUnicodeNumberTTStart = 0x1D7F6

// mathtt (monospace)
internal fun getTypewriter(ch: Char): MTCodepointChar {
    when {
        isUpperEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathCapitalTTStart + (ch - 'A'))
        }

        isLowerEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathLowerTTStart + (ch - 'a'))
        }

        isNumber(ch) -> {
            return MTCodepointChar(kMTUnicodeNumberTTStart + (ch - '0'))
        }

        else -> {
            // Monospace characters don't exist for greek, we give them the
            // default treatment.
            return getDefaultStyle(ch)
        }
    }
}

internal const val kMTUnicodeMathCapitalSansSerifStart = 0x1D5A0
internal const val kMTUnicodeMathLowerSansSerifStart = 0x1D5BA
internal const val kMTUnicodeNumberSansSerifStart = 0x1D7E2

// mathsf
internal fun getSansSerif(ch: Char): MTCodepointChar {
    when {
        isUpperEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathCapitalSansSerifStart + (ch - 'A'))
        }

        isLowerEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathLowerSansSerifStart + (ch - 'a'))
        }

        isNumber(ch) -> {
            return MTCodepointChar(kMTUnicodeNumberSansSerifStart + (ch - '0'))
        }

        else -> {
            // Sans-serif characters don't exist for greek, we give them the
            // default treatment.
            return getDefaultStyle(ch)
        }
    }
}

internal const val kMTUnicodeMathCapitalFrakturStart = 0x1D504
internal const val kMTUnicodeMathLowerFrakturStart = 0x1D51E

// mathfrak
internal fun getFraktur(ch: Char): MTCodepointChar {
    // Fraktur has exceptions:
    when (ch) {
        'C' ->
            return MTCodepointChar(0x212D)   // C Fraktur
        'H' ->
            return MTCodepointChar(0x210C)   // Hilbert space
        'I' ->
            return MTCodepointChar(0x2111)   // Imaginary
        'R' ->
            return MTCodepointChar(0x211C)   // Real
        'Z' ->
            return MTCodepointChar(0x2128)   // Z Fraktur
    }
    if (isUpperEn(ch)) {
        return MTCodepointChar(kMTUnicodeMathCapitalFrakturStart + (ch - 'A'))
    } else if (isLowerEn(ch)) {
        return MTCodepointChar(kMTUnicodeMathLowerFrakturStart + (ch - 'a'))
    }
    // Fraktur characters don't exist for greek & numbers, we give them the
    // default treatment.
    return getDefaultStyle(ch)
}

internal const val kMTUnicodeMathCapitalBlackboardStart = 0x1D538
internal const val kMTUnicodeMathLowerBlackboardStart = 0x1D552
internal const val kMTUnicodeNumberBlackboardStart = 0x1D7D8

// mathbb (double struck)
internal fun getBlackboard(ch: Char): MTCodepointChar {
    // Blackboard has lots of exceptions:
    when (ch) {
        'C' ->
            return MTCodepointChar(0x2102)  // Complex numbers
        'H' ->
            return MTCodepointChar(0x210D)  // Quaternions
        'N' ->
            return MTCodepointChar(0x2115)   // Natural numbers
        'P' ->
            return MTCodepointChar(0x2119)   // Primes
        'Q' ->
            return MTCodepointChar(0x211A)   // Rationals
        'R' ->
            return MTCodepointChar(0x211D)   // Reals
        'Z' ->
            return MTCodepointChar(0x2124)  // Integers
    }
    when {
        isUpperEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathCapitalBlackboardStart + (ch - 'A'))
        }

        isLowerEn(ch) -> {
            return MTCodepointChar(kMTUnicodeMathLowerBlackboardStart + (ch - 'a'))
        }

        isNumber(ch) -> {
            return MTCodepointChar(kMTUnicodeNumberBlackboardStart + (ch - '0'))
        }
    }
    // Blackboard characters don't exist for greek, we give them the
    // default treatment.
    return getDefaultStyle(ch)
}

internal fun styleCharacter(ch: Char, fontStyle: MTFontStyle): MTCodepointChar {
    when (fontStyle) {
        MTFontStyle.KMTFontStyleDefault -> {
            return getDefaultStyle(ch)
        }

        MTFontStyle.KMTFontStyleRoman -> {
            return MTCodepointChar(ch.code)
        }

        MTFontStyle.KMTFontStyleBold -> {
            return getBold(ch)
        }

        MTFontStyle.KMTFontStyleItalic -> {
            return getItalicized(ch)
        }

        MTFontStyle.KMTFontStyleBoldItalic -> {
            return getBoldItalic(ch)
        }

        MTFontStyle.KMTFontStyleCaligraphic -> {
            return getCalligraphicChar(ch)
        }

        MTFontStyle.KMTFontStyleTypewriter -> {
            return getTypewriter(ch)
        }

        MTFontStyle.KMTFontStyleSansSerif -> {
            return getSansSerif(ch)
        }

        MTFontStyle.KMTFontStyleFraktur -> {
            return getFraktur(ch)
        }

        MTFontStyle.KMTFontStyleBlackboard -> {
            return getBlackboard(ch)
        }
//        else -> {
//            throw MathDisplayException("Unknown style $fontStyle for font.")
//
//        }
    }
}

// This can only take single unicode character sequence as input.
// Should never be called with a codepoint that requires 2 escaped characters to represent
internal fun changeFont(str: String, fontStyle: MTFontStyle): String {
    val ret = StringBuilder()
    val ca = str.toCharArray()
    for (ch in ca) {
        val codepoint = styleCharacter(ch, fontStyle)
        ret.append(codepoint.toUnicodeString())
    }
    return ret.toString()
}
