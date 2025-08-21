package com.agog.mathdisplay.render

const val multiplication = '\u00D7'                     // ×
const val division = '\u00F7'                           // ÷
const val fractionSlash = '\u2044'                      // ⁄
const val whiteSquare = '\u25A1'                        // □
const val blackSquare = '\u25A0'                        // ■
const val lessEqual = '\u2264'                          // ≤
const val greaterEqual = '\u2265'                       // ≥
const val notEqual = '\u2260'                           // ≠
const val squareRoot = '\u221A'                         // √
const val cubeRoot = '\u221B'                           // ∛
const val infinity = '\u221E'                           // ∞
const val angle = '\u2220'                              // ∠
const val degree = '\u00B0'                             // °

const val capitalGreekStart = '\u0391'                  // Α
const val capitalGreekEnd = '\u03A9'                    // Ω
const val lowerGreekStart = '\u03B1'                    // α
const val lowerGreekEnd = '\u03C9'                      // ω

// mathit
const val planksConstant = 0x210E                     // ℎ

// 高位字符
const val lowerItalicStart = 0x1D44E                    // 𝑎
const val capitalItalicStart = 0x1D434                  // 𝐴
const val greekLowerItalicStart = 0x1D6FC               // 𝞼
const val greekCapitalItalicStart = 0x1D6E2             // 𝞢
const val greekSymbolItalicStart = 0x1D716              // 𝞖

// mathbf
const val mathCapitalBoldStart = 0x1D400                // 𝐀
const val mathLowerBoldStart = 0x1D41A                  // 𝐚
const val greekCapitalBoldStart = 0x1D6A8               // 𝚨
const val greekLowerBoldStart = 0x1D6C2                 // 𝚲
const val greekSymbolBoldStart = 0x1D6DC                // 𝞜
const val numberBoldStart = 0x1D7CE                     // 𝟎

// mathbfit
const val mathCapitalBoldItalicStart = 0x1D468          // 𝑨
const val mathLowerBoldItalicStart = 0x1D482            // 𝑎
const val greekCapitalBoldItalicStart = 0x1D71C         // 𝞜
const val greekLowerBoldItalicStart = 0x1D736           // 𝞼
const val greekSymbolBoldItalicStart = 0x1D750          // 𝞸


const val mathCapitalScriptStart = 0x1D49C              // 𝒜

// mathtt (monospace)
const val mathCapitalTTStart = 0x1D670                  // 𝙰
const val mathLowerTTStart = 0x1D68A                    // 𝚊
const val numberTTStart = 0x1D7F6                       // 𝟶

// mathsf
const val mathCapitalSansSerifStart = 0x1D5A0           // 𝗔
const val mathLowerSansSerifStart = 0x1D5BA             // 𝗮
const val numberSansSerifStart = 0x1D7E2                // 𝟢

// mathfrak
const val mathCapitalFrakturStart = 0x1D504             // 𝔄
const val mathLowerFrakturStart = 0x1D51E               // 𝔞

// mathbb (double struck)
const val mathCapitalBlackboardStart = 0x1D538          // 𝔸
const val mathLowerBlackboardStart = 0x1D552            // 𝕒
const val numberBlackboardStart = 0x1D7D8               // 𝟘


// Note this is not equivalent to ch.isLowerCase() delta is a test case
internal fun Char.isLowerEn(): Boolean {
    return this >= 'a' && this <= 'z'
}

internal fun Char.isUpperEn(): Boolean {
    return this >= 'A' && this <= 'Z'
}

internal fun Char.isNumber(): Boolean {
    return this >= '0' && this <= '9'
}

/**
 * 判断当前字符是否为希腊字母的小写字母。
 *
 * @return 如果字符在希腊字母的小写范围内返回 true，否则返回 false。
 */
internal fun Char.isLowerGreek(): Boolean {
    return this >= lowerGreekStart && this <= lowerGreekEnd
}

/**
 * 检查是否为希腊大写字母字符。
 *
 * @return 如果字符是希腊大写字母，则返回true；否则返回false。
 */
internal fun Char.isCapitalGreek(): Boolean {
    return this >= capitalGreekStart && this <= capitalGreekEnd
}


/**
 * 返回给定字符在希腊符号特定顺序中的位置。
 *
 * @return 如果字符是特定的希腊符号之一，则返回其在数组中的索引；否则返回 -1。
 */
internal fun Char.greekSymbolOrder(): Int {
    // These greek symbols that always appear in unicode in this particular order after the alphabet
    // The symbols are epsilon, vartheta, varkappa, phi, varrho, varpi.
    val greekSymbols = arrayOf(0x03F5, 0x03D1, 0x03F0, 0x03D5, 0x03F1, 0x03D6)
    return greekSymbols.indexOf(code)
}

/**
 * 判断给定字符是否为希腊符号。
 *
 * @return 如果字符是某个特定的希腊符号，则返回 true；否则返回 false。
 */
internal fun Char.isGreekSymbol(): Boolean {
    return this.greekSymbolOrder() != -1
}