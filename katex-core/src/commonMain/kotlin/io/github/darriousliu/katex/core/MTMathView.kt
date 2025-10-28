package io.github.darriousliu.katex.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import io.github.darriousliu.katex.mathdisplay.parse.*
import io.github.darriousliu.katex.mathdisplay.render.MTFont
import io.github.darriousliu.katex.mathdisplay.render.MTMathListDisplay
import io.github.darriousliu.katex.mathdisplay.render.MTTypesetter
import io.github.darriousliu.katex.mathdisplay.render.MTFontManager

/**
 * 共享数据类，用于管理 MTMathView 的状态
 */
@Stable
private data class MTMathViewState(
    val displayList: MTMathListDisplay? = null,
    val calculatedWidth: Dp = 0.dp,
    val calculatedHeight: Dp = 0.dp,
    val parseError: MTParseError? = null,
)

@Stable
data class MathItem(
    val displayList: MTMathListDisplay?, // 预计算的显示对象
    val width: Float,
    val height: Float,
    val latex: String,
    val parseError: MTParseError? = null,
)

/**
 * 显示数学公式的组件，通过指定的公式对象、字体、颜色等属性来渲染相应的内容。
 *
 * @param mathList 数学公式对象，定义需要显示的数学公式。如果为空，则不渲染公式。
 * @param modifier 修饰符。
 * @param parseError 解析错误信息，当存在公式解析错误时用于描述具体的错误内容。
 * @param fontSize 公式中字体的大小，默认使用组件内部定义的默认字体大小。
 * @param textColor 公式渲染的颜色，默认为黑色。
 * @param font 数学公式的字体类型，若为 null 则使用默认数学字体。
 * @param mode 数学公式的显示模式，包括“Display”模式和“Text”模式。
 * @param textAlignment 数学公式的对齐方式，可设置为左对齐、右对齐或居中显示。
 * @param displayErrorInline 是否内联显示解析错误，当解析错误存在且该值为 true 时显示错误信息。
 * @param errorFontSize 错误信息字体的大小，仅在解析错误需要显示时有效。
 */
@Composable
fun MTMathView(
    mathList: MTMathList?,
    modifier: Modifier = Modifier,
    parseError: MTParseError? = null,
    fontSize: TextUnit = DefaultFontSize,
    textColor: Color = Color.Black,
    font: MTFont? = null,
    mode: MTMathViewMode = MTMathViewMode.KMTMathViewModeDisplay,
    textAlignment: MTTextAlignment = MTTextAlignment.KMTTextAlignmentLeft,
    displayErrorInline: Boolean = false,
    errorFontSize: TextUnit = DefaultErrorFontSize,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    // 状态管理
    val state = remember(mathList, parseError, fontSize, font, mode, displayErrorInline) {
        with(density) {
            // 1. 更新字体
            val newFont = (font ?: MTFontManager.defaultFont()).copyFontWithSize(fontSize.toPx())

            // 2. 更新显示列表
            val newDisplayList = if (mathList != null) {
                val style = when (mode) {
                    MTMathViewMode.KMTMathViewModeDisplay -> MTLineStyle.KMTLineStyleDisplay
                    MTMathViewMode.KMTMathViewModeText -> MTLineStyle.KMTLineStyleText
                }
                MTTypesetter.createLineForMathList(mathList, newFont, style)
            } else {
                null
            }

            // 3. 计算尺寸
            val (width, height) = when {
                newDisplayList != null -> {
                    val width = (newDisplayList.width + 1).toDp()
                    val height = (newDisplayList.ascent + newDisplayList.descent + 1).toDp()
                    Pair(width, height)
                }

                parseError != null && parseError.errorCode != MTParseErrors.ErrorNone && displayErrorInline -> {
                    // 计算错误文本尺寸
                    val errorTextSizeDp = errorFontSize.toDp()
                    val estimatedWidth = parseError.errorDesc.length * errorTextSizeDp * 0.6f
                    Pair(estimatedWidth, errorTextSizeDp * 1.2f)
                }

                else -> Pair(0.dp, 0.dp)
            }

            // 4. 一次性更新所有状态，避免中间状态
            MTMathViewState(
                displayList = newDisplayList,
                calculatedWidth = width,
                calculatedHeight = height,
                parseError = parseError
            )
        }
    }

    MathCanvas(
        parseError = state.parseError,
        displayList = state.displayList,
        modifier = modifier.size(state.calculatedWidth, state.calculatedHeight),
        textColor = textColor,
        textAlignment = textAlignment,
        errorFontSize = errorFontSize,
        textMeasurer = textMeasurer
    )
}

/**
 * Compose 版本的数学公式显示组件
 *
 * 支持 LaTeX 格式的数学公式渲染
 *
 * @param latex LaTeX 格式的数学公式字符串
 * @param modifier Modifier
 * @param fontSize 字体大小（以 sp 为单位）
 * @param textColor 文本颜色
 * @param font 字体，默认使用 MTFontManager.defaultFont()
 * @param mode 显示模式：Display 模式或 Text 模式
 * @param textAlignment 文本对齐方式
 * @param displayErrorInline 是否内联显示解析错误
 * @param errorFontSize 错误文本的字体大小
 */
@Composable
fun MTMathView(
    latex: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = DefaultFontSize,
    textColor: Color = Color.Black,
    font: MTFont? = null,
    mode: MTMathViewMode = MTMathViewMode.KMTMathViewModeDisplay,
    textAlignment: MTTextAlignment = MTTextAlignment.KMTTextAlignmentLeft,
    displayErrorInline: Boolean = true,
    errorFontSize: TextUnit = DefaultErrorFontSize,
) {
    // 解析状态
    var parseError by remember { mutableStateOf(MTParseError()) }
    val mathList = remember(latex, fontSize, textColor, font, mode, errorFontSize) {
        if (latex.isNotEmpty()) {
            // 解析 LaTeX 字符串
            val (list, error) = parseMathList(latex)
            parseError = error
            list
        } else {
            null
        }
    }


    MTMathView(
        mathList = mathList,
        parseError = parseError,
        modifier = modifier,
        fontSize = fontSize,
        textColor = textColor,
        font = font,
        mode = mode,
        textAlignment = textAlignment,
        displayErrorInline = displayErrorInline,
        errorFontSize = errorFontSize,
    )
}

/**
 * 外部计算MTMathListDisplay后直接渲染数学公式到Canvas的组件，
 * 在Lazy组件中推荐使用本组件，避免解析计算。
 *
 * @param mathItem 数学公式对象，包含LaTeX字符串、宽高等信息。
 * @param modifier 修饰符。
 * @param textAlignment 文本对齐方式，可设置为左对齐、居中或右对齐，默认值为左对齐（KMTTextAlignmentLeft）。
 * @param errorFontSize 错误信息的字体大小，仅在渲染错误信息时使用，默认值为DefaultErrorFontSize。
 */
@Composable
fun MTMathView(
    mathItem: MathItem,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    textAlignment: MTTextAlignment = MTTextAlignment.KMTTextAlignmentLeft,
    errorFontSize: TextUnit = DefaultErrorFontSize,
) {
    val density = LocalDensity.current
    val width = with(density) { mathItem.width.toDp() }
    val height = with(density) { mathItem.height.toDp() }
    val textMeasurer = rememberTextMeasurer()

    MathCanvas(
        parseError = mathItem.parseError,
        displayList = mathItem.displayList,
        modifier = modifier.size(width, height),
        textColor = textColor,
        textAlignment = textAlignment,
        errorFontSize = errorFontSize,
        textMeasurer = textMeasurer
    )
}

@Composable
private fun MathCanvas(
    parseError: MTParseError?,
    displayList: MTMathListDisplay?,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    textAlignment: MTTextAlignment = MTTextAlignment.KMTTextAlignmentLeft,
    errorFontSize: TextUnit = DefaultErrorFontSize,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
) {
    Canvas(
        modifier = modifier
    ) {
        when {
            parseError != null && parseError.errorCode != MTParseErrors.ErrorNone -> {
                // 绘制错误信息
                drawError(
                    errorMessage = parseError.errorDesc,
                    errorFontSize = errorFontSize,
                    errorColor = Color.Red,
                    textMeasurer = textMeasurer
                )
            }

            displayList != null -> {
                // 绘制数学公式
                drawMathFormula(
                    displayList = displayList,
                    textColor = textColor,
                    textAlignment = textAlignment
                )
            }
        }
    }
}


private fun parseMathList(latex: String): Pair<MTMathList?, MTParseError> {
    return if (latex.isNotEmpty()) {
        val parseError = MTParseError()
        val list = MTMathListBuilder.buildFromString(latex, parseError)
        val mathList = if (parseError.errorCode != MTParseErrors.ErrorNone) {
            null
        } else {
            list
        }
        mathList to parseError
    } else {
        null to MTParseError()
    }
}

/**
 * 绘制数学公式
 */
private fun DrawScope.drawMathFormula(
    displayList: MTMathListDisplay,
    textColor: Color,
    textAlignment: MTTextAlignment
) {
    displayList.textColor = textColor

    // 根据对齐方式计算 X 位置
    val textX = when (textAlignment) {
        MTTextAlignment.KMTTextAlignmentLeft -> 0f
        MTTextAlignment.KMTTextAlignmentCenter ->
            (size.width - displayList.width) / 2

        MTTextAlignment.KMTTextAlignmentRight ->
            size.width - displayList.width
    }

    // 计算 Y 位置（垂直居中）
    var eqHeight = displayList.ascent + displayList.descent
    if (eqHeight < size.height / 2) {
        eqHeight = size.height / 2
    }
    val textY = (size.height - eqHeight) / 2 + displayList.descent

    // 设置位置
    displayList.position.x = textX
    displayList.position.y = textY

    // 绘制（翻转 Y 轴以匹配数学坐标系）
    scale(1f, -1f) {
        displayList.draw(drawContext.canvas)
    }
}

/**
 * 绘制错误信息
 */
private fun DrawScope.drawError(
    errorMessage: String,
    errorFontSize: TextUnit,
    errorColor: Color,
    textMeasurer: TextMeasurer
) {
    drawText(
        textMeasurer = textMeasurer,
        text = errorMessage,
        style = TextStyle(
            color = errorColor,
            fontSize = errorFontSize,
            textAlign = TextAlign.Center,
        ),
        size = size
    )
}

/**
 * 数学显示模式
 */
enum class MTMathViewMode {
    /// Display 模式，相当于 TeX 中的 $$
    KMTMathViewModeDisplay,

    /// Text 模式，相当于 TeX 中的 $
    KMTMathViewModeText
}

/**
 * 文本对齐方式
 */
enum class MTTextAlignment {
    /// 左对齐
    KMTTextAlignmentLeft,

    /// 居中对齐
    KMTTextAlignmentCenter,

    /// 右对齐
    KMTTextAlignmentRight
}

private val DefaultFontSize = 20.sp
private val DefaultErrorFontSize = 20.sp