package io.github.darriousliu.katex.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import io.github.darriousliu.katex.mathdisplay.render.MTFont
import io.github.darriousliu.katex.mathdisplay.render.MTFontManager
import io.github.darriousliu.katex.mathdisplay.render.MTMathFont

/**
 * 提供一个通过指定字体大小和数学字体类型获取 `MTFont` 对象的记忆化函数。
 *
 * @param fontSize 字体大小，以 `TextUnit` 表示。
 * @param mathFont 数学字体类型，默认值为 `MTMathFont.LatinModernMath`。
 * @return 返回一个 `MTFont` 对象，其中包含指定大小和字体类型的数学字体数据。
 */
@Composable
fun rememberMTFont(
    fontSize: TextUnit,
    mathFont: MTMathFont = MTMathFont.LatinModernMath,
): MTFont {
    val density = LocalDensity.current
    return remember(fontSize, mathFont, density) {
        with(density) {
            MTFontManager.fontWithSize(fontSize.toPx(), mathFont)
        }
    }
}

/**
 * 记住指定字体并返回其状态。此方法用于在组合环境中加载自定义字体并管理其状态。
 *
 * @param fontSize 指定字体的大小，使用 [TextUnit] 表示。
 * @param fontName 字体的名称，用于识别需要加载的字体。
 * @param loadFontData 一个挂起函数，用于异步加载字体的二进制数据。
 * @return 返回一个 [State] 对象，包含加载的 [MTFont] 对象。如果加载失败，返回值为 null。
 */
@Composable
fun rememberMTFont(
    fontSize: TextUnit,
    fontName: String,
    loadFontData: suspend () -> ByteArray,
): State<MTFont?> {
    val density = LocalDensity.current
    return produceState(null, fontSize, fontName, density) {
        with(density) {
            value = MTFontManager.fontWithData(fontSize.toPx(), fontName, loadFontData)
        }
    }
}