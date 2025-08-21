package com.agog.mathdisplay.render

import com.agog.mathdisplay.parse.MTFontStyle
import com.agog.mathdisplay.parse.MathDisplayException
import com.pvporbit.freetype.Utils

data class CGGlyph(
    var gid: Int = 0,
    var glyphAscent: Float = 0.0f,
    var glyphDescent: Float = 0.0f,
    var glyphWidth: Float = 0.0f
) {
    val isValid: Boolean
        get() = gid != 0
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
internal fun getItalicized(ch: Char): MTCodepointChar {
    // Special cases for italics
    when {
        ch == 'h' -> {  // italic h (plank's constant)
            return MTCodepointChar(planksConstant)
        }

        ch.isUpperEn() -> {
            return MTCodepointChar(capitalItalicStart + (ch - 'A'))
        }

        ch.isLowerEn() -> {
            return MTCodepointChar(lowerItalicStart + (ch - 'a'))
        }

        ch.isCapitalGreek() -> {
            // Capital Greek characters
            return MTCodepointChar(greekCapitalItalicStart + (ch - capitalGreekStart))
        }

        ch.isLowerGreek() -> {
            // Greek characters
            return MTCodepointChar(greekLowerItalicStart + (ch - lowerGreekStart))
        }

        ch.isGreekSymbol() -> {
            return MTCodepointChar(greekSymbolItalicStart + ch.greekSymbolOrder())
        }
    }
    // Note there are no italicized numbers in unicode so we don't support italicizing numbers.
    return MTCodepointChar(ch.code)
}

// mathbf
internal fun getBold(ch: Char): MTCodepointChar {
    when {
        ch.isUpperEn() -> {
            return MTCodepointChar(mathCapitalBoldStart + (ch - 'A'))
        }

        ch.isLowerEn() -> {
            return MTCodepointChar(mathLowerBoldStart + (ch - 'a'))
        }

        ch.isCapitalGreek() -> {
            // Capital Greek characters
            return MTCodepointChar(greekCapitalBoldStart + (ch - capitalGreekStart))
        }

        ch.isLowerGreek() -> {
            // Greek characters
            return MTCodepointChar(greekLowerBoldStart + (ch - lowerGreekStart))
        }

        ch.isGreekSymbol() -> {
            return MTCodepointChar(greekSymbolBoldStart + ch.greekSymbolOrder())
        }

        ch.isNumber() -> {
            return MTCodepointChar(numberBoldStart + (ch - '0'))
        }
    }
    return MTCodepointChar(ch.code)
}

// mathbfit
internal fun getBoldItalic(ch: Char): MTCodepointChar {
    when {
        ch.isUpperEn() -> {
            return MTCodepointChar(mathCapitalBoldItalicStart + (ch - 'A'))
        }

        ch.isLowerEn() -> {
            return MTCodepointChar(mathLowerBoldItalicStart + (ch - 'a'))
        }

        ch.isCapitalGreek() -> {
            // Capital Greek characters
            return MTCodepointChar(greekCapitalBoldItalicStart + (ch - capitalGreekStart))
        }

        ch.isLowerGreek() -> {
            // Greek characters
            return MTCodepointChar(greekLowerBoldItalicStart + (ch - lowerGreekStart))
        }

        ch.isGreekSymbol() -> {
            return MTCodepointChar(greekSymbolBoldItalicStart + ch.greekSymbolOrder())
        }

        ch.isNumber() -> {
            // No bold italic for numbers so we just bold them.
            return getBold(ch)
        }
    }
    return MTCodepointChar(ch.code)
}

// LaTeX default
internal fun getDefaultStyle(ch: Char): MTCodepointChar {
    when {
        ch.isLowerEn() || ch.isUpperEn() || ch.isLowerGreek() || ch.isGreekSymbol() -> {
            return getItalicized(ch)
        }

        ch.isNumber() || ch.isCapitalGreek() -> {
            return MTCodepointChar(ch.code)
        }

        ch == '.' -> {
            // . is treated as a number in our code, but it doesn't change fonts.
            return MTCodepointChar(ch.code)
        }
    }
    throw MathDisplayException("Unknown character $ch for default style.")
}

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
        ch.isUpperEn() -> {
            return MTCodepointChar(mathCapitalScriptStart + (ch - 'A'))
        }

        ch.isLowerEn() -> {
            // Latin Modern Math does not have lower case calligraphic characters, so we use
            // the default style instead of showing a ?
            return getDefaultStyle(ch)
        }
    }
    // Calligraphic characters don't exist for greek or numbers, we give them the
    // default treatment.
    return getDefaultStyle(ch)
}

// mathtt (monospace)
internal fun getTypewriter(ch: Char): MTCodepointChar {
    when {
        ch.isUpperEn() -> {
            return MTCodepointChar(mathCapitalTTStart + (ch - 'A'))
        }

        ch.isLowerEn() -> {
            return MTCodepointChar(mathLowerTTStart + (ch - 'a'))
        }

        ch.isNumber() -> {
            return MTCodepointChar(numberTTStart + (ch - '0'))
        }

        else -> {
            // Monospace characters don't exist for greek, we give them the
            // default treatment.
            return getDefaultStyle(ch)
        }
    }
}

// mathsf
internal fun getSansSerif(ch: Char): MTCodepointChar {
    when {
        ch.isUpperEn() -> {
            return MTCodepointChar(mathCapitalSansSerifStart + (ch - 'A'))
        }

        ch.isLowerEn() -> {
            return MTCodepointChar(mathLowerSansSerifStart + (ch - 'a'))
        }

        ch.isNumber() -> {
            return MTCodepointChar(numberSansSerifStart + (ch - '0'))
        }

        else -> {
            // Sans-serif characters don't exist for greek, we give them the
            // default treatment.
            return getDefaultStyle(ch)
        }
    }
}

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
    if (ch.isUpperEn()) {
        return MTCodepointChar(mathCapitalFrakturStart + (ch - 'A'))
    } else if (ch.isLowerEn()) {
        return MTCodepointChar(mathLowerFrakturStart + (ch - 'a'))
    }
    // Fraktur characters don't exist for greek & numbers, we give them the
    // default treatment.
    return getDefaultStyle(ch)
}

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
        ch.isUpperEn() -> {
            return MTCodepointChar(mathCapitalBlackboardStart + (ch - 'A'))
        }

        ch.isLowerEn() -> {
            return MTCodepointChar(mathLowerBlackboardStart + (ch - 'a'))
        }

        ch.isNumber() -> {
            return MTCodepointChar(numberBlackboardStart + (ch - '0'))
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
