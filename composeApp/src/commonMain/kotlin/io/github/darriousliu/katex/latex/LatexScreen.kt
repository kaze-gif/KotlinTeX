package io.github.darriousliu.katex.latex

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.darriousliu.katex.core.MTMathView
import io.github.darriousliu.katex.core.MTMathViewMode
import io.github.darriousliu.katex.mathdisplay.render.MTFontManager
import io.github.darriousliu.katex.mathdisplay.render.MTMathFont
import org.koin.compose.viewmodel.koinViewModel

enum class LatexType {
    MATH_ITEMS, // 用于显示 MathItem 列表
    MATH_LIST,  // 用于显示 MathList 列表
    LATEX,
}

val latexType = LatexType.MATH_LIST

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LatexScreen(
    modifier: Modifier = Modifier,
    viewModel: LatexViewModel = koinViewModel<LatexViewModel>()
) {
    val density = LocalDensity.current
    var fontSize by remember { mutableStateOf(20.sp) }
    val fontSizePx = with(density) { fontSize.toPx() }

    var mtFont by remember {
        mutableStateOf(MTFontManager.defaultFont())
    }
    var currentFont by remember { mutableIntStateOf(0) }

    var mode by remember { mutableStateOf(MTMathViewMode.KMTMathViewModeDisplay) }
    var color by remember { mutableStateOf(Color.Black) }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("KotlinTex") },
                actions = {
                    // 改变字体
                    MenuAction(
                        onClick = {
                            currentFont = it ?: 0
                            mtFont = when (it) {
                                0 -> MTFontManager.fontWithSize(fontSizePx, MTMathFont.LatinModernMath)
                                1 -> MTFontManager.fontWithSize(
                                    fontSizePx,
                                    MTMathFont.TexGyreTermsMath
                                )
                                2 -> MTFontManager.fontWithSize(fontSizePx, MTMathFont.XitsMath)
                                else -> mtFont
                            }
                        },
                        text = "Font",
                        current = currentFont,
                        menus = listOf(
                            "Latin Modern Math" to 0,
                            "Tex Gyre Terms" to 1,
                            "XITS Math" to 2,
                        )
                    )
                    // 改变字体大小
                    MenuAction(
                        onClick = {
                            fontSize = it ?: 20.sp
                        },
                        text = "Font Size",
                        current = fontSize,
                        menus = listOf(
                            "20sp" to 20.sp,
                            "24sp" to 24.sp,
                            "28sp" to 28.sp,
                            "32sp" to 32.sp,
                            "36sp" to 36.sp,
                            "40sp" to 40.sp
                        )
                    )
                    MenuAction(
                        onClick = {
                            mode = it ?: MTMathViewMode.KMTMathViewModeDisplay
                        },
                        text = "Mode",
                        current = mode,
                        menus = listOf(
                            "Display" to MTMathViewMode.KMTMathViewModeDisplay,
                            "Text" to MTMathViewMode.KMTMathViewModeText,
                        )
                    )
                    MenuAction(
                        onClick = {
                            color = it ?: Color.Black
                        },
                        text = "Text Color",
                        current = color,
                        menus = listOf(
                            "Black" to Color.Black,
                            "Red" to Color.Red,
                            "Green" to Color.Green,
                            "Blue" to Color.Blue,
                            "Gray" to Color.Gray,
                            "White" to Color.White,
                            "Yellow" to Color.Yellow,
                            "Cyan" to Color.Cyan,
                            "Magenta" to Color.Magenta,
                            "Pink" to Color(0xFFFFC0CB), // 粉色
                            "Orange" to Color(0xFFFFA500), // 橙色
                            "Purple" to Color(0xFF800080), // 紫色
                            "Brown" to Color(0xFFA52A2A), // 棕色
                            "Teal" to Color(0xFF008080), // 水鸭色
                            "Lime" to Color(0xFF00FF00), // 青柠
                            "Olive" to Color(0xFF808000), // 橄
                            "Navy" to Color(0xFF000080), // 海军蓝
                            "Maroon" to Color(0xFF800000), // 栗色
                            "Gold" to Color(0xFFFFD700), // 金色
                            "Silver" to Color(0xFFC0C0C0), // 银色
                            "Lavender" to Color(0xFFE6E6FA), // 薰衣草色
                            "Coral" to Color(0xFFFF7F50), // 珊瑚色
                            "Sky Blue" to Color(0xFF87CEEB), // 天空蓝
                            "Light Green" to Color(0xFF90EE90), // 浅绿色
                            "Dark Gray" to Color(0xFFA9A9A9A), // 深灰色
                            "Dark Red" to Color(0xFF8B0000), // 深红色
                            "Dark Green" to Color(0xFF006400), // 深绿色
                            "Dark Blue" to Color(0xFF00008B), // 深蓝色
                            "Dark Cyan" to Color(0xFF008B8B), // 深水鸭色
                        )
                    )
                }
            )
        },
    ) {
        LaunchedEffect(Unit) {
            when (latexType) {
                LatexType.MATH_ITEMS -> {
                    viewModel.parseMathItems()
                }

                LatexType.MATH_LIST -> {
                    viewModel.parseMathList()
                }

                LatexType.LATEX -> {
                    viewModel.parseLatexList()
                }
            }
        }

        val mathList by viewModel.mathList.collectAsStateWithLifecycle()
        val mathItems by viewModel.mathItems.collectAsStateWithLifecycle()
        val latexList by viewModel.latexList.collectAsStateWithLifecycle()
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            when (latexType) {
                LatexType.MATH_ITEMS -> {
                    itemsIndexed(
                        items = mathItems,
                        key = { index, _ -> index }
                    ) { _, item ->
                        MTMathView(
                            mathItem = item,
                            textColor = color,
                        )
                    }
                }

                LatexType.MATH_LIST -> {
                    itemsIndexed(
                        items = mathList,
                        key = { index, _ -> index }
                    ) { _, math ->
                        MTMathView(
                            mathList = math,
                            fontSize = fontSize,
                            font = mtFont,
                            mode = mode,
                            textColor = color,
                        )
                    }
                }

                LatexType.LATEX -> {
                    itemsIndexed(
                        items = latexList,
                        key = { index, _ -> index }
                    ) { _, item ->
                        MTMathView(
                            latex = item,
                            fontSize = fontSize,
                            font = mtFont,
                            mode = mode,
                            textColor = color,
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun <T> MenuAction(
    onClick: (T?) -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    current: T? = null,
    menus: List<Pair<String, T>>? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    Button(
        onClick = {
            if (menus == null) {
                onClick(null)
            } else {
                expanded = true
            }
        },
    ) {
        Text(text)
    }
    if (menus != null) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
        ) {
            menus.forEach { menu ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(menu.first)
                            Checkbox(
                                checked = current == menu.second,
                                onCheckedChange = null, // 复选框不需要交互
                            )
                        }
                    },
                    onClick = {
                        onClick(menu.second)
                        expanded = false
                    }
                )
            }
        }
    }
}
