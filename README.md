# KotlinTeX

[English Version](README-en.md) | [中文版本](README.md)

[![许可证](https://img.shields.io/badge/License-BSD%202--Clause-orange.svg)](https://opensource.org/licenses/BSD-2-Clause)
[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-blue.svg?logo=kotlin)]([http://kotlinlang.org](https://www.jetbrains.com/kotlin-multiplatform/))

一个基于 Kotlin Multiplatform 的跨平台 LaTeX 数学表达式渲染库，支持 Android / iOS / Jvm 平台。

## 效果展示

https://github.com/user-attachments/assets/b240a3e7-7a29-437d-8f36-2c248b55669c

## 关于项目

本项目基于开源项目 [**AndroidMath**](https://github.com/gregcockroft/AndroidMath)
改写而来，将所有代码转换为 Kotlin 并使用 Kotlin Multiplatform 技术实现跨平台支持。通过 Compose
Multiplatform 为 Android / iOS / Jvm 平台提供高质量的 LaTeX 数学表达式渲染功能。

## 特性

- 🚀 基于 Kotlin Multiplatform 技术
    - Android 通过 JNI 与 FreeType 库集成
    - iOS 通过 C interop 与 FreeType 库集成
    - JVM 平台通过 lwjgl 库与 FreeType 库集成
- 📱 支持 Android / iOS / Jvm 平台
- 🎨 使用 Compose Multiplatform 进行 UI 渲染
- 📊 完整的 LaTeX 数学表达式支持
- 🔧 易于集成和使用

## 平台支持

- ✅ Android (API 23+)，已适配16KB Page Size
- ✅ iOS (iOS 13+)
- ✅ JVM (Compose Multiplatform 桌面应用)

## 依赖

### Gradle (Kotlin DSL)

将以下内容添加到 `settings.gradle.kts`：

```kotlin
pluginManagement {
    repositories {
        mavenCentral() // 或者 maven { url = uri("https://jitpack.io") }
    }
}
```

然后，在你的 `build.gradle.kts` 中添加依赖：

### Android

```kotlin
dependencies {
    implementation("io.github.darriousliu:katex-core:0.3.1")
}
```

### Kotlin 多平台

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.darriousliu:katex-core:0.3.1")
        }
    }
}
```

### 项目配置

请确保您的项目已正确配置 Kotlin Multiplatform 和 Compose Multiplatform。

### JVM平台特定配置

在JVM平台上使用时，需要添加平台特定的FreeType本地库依赖：

```kotlin
jvmMain.dependencies {
    implementation(compose.desktop.currentOs)
    // 检测平台
    val currentOs = OperatingSystem.current()
    val lwjglPlatform = when {
        currentOs.isMacOsX -> "macos"
        currentOs.isWindows -> "windows"
        currentOs.isLinux -> "linux"
        else -> error("Unsupported OS: $currentOs")
    }
    val processor = ArchUtils.getProcessor()
    val lwjglArch = when {
        processor.isAarch64 -> "arm64"
        processor.is64Bit -> "x64"
        else -> error("Unsupported architecture: ${processor.arch} ${processor.type}")
    }
    val lwjglNatives = "natives-$lwjglPlatform-$lwjglArch"
    // 平台特定的本地库
    runtimeOnly("org.lwjgl:lwjgl:${版本号}:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-freetype:${版本号}:$lwjglNatives")
}
```

## 使用方法

1. 直接使用Latex字符串

```kotlin
@Composable
fun LatexExample() {
    MTMathView(
        latex = "\\frac{a}{b}",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 20.sp,
        textColor = Color.Black,
        font = null,
        mode = MTMathViewMode.KMTMathViewModeDisplay,
        textAlignment = MTTextAlignment.KMTTextAlignmentLeft,
        displayErrorInline = true,
        errorFontSize = 20.sp,
    )
}
```

2. 在后台线程如 ViewModel 中解析Latex得到

```kotlin
val latexFormulas = listOf(
    "\\frac{a}{b}",
    "\\sqrt{x^2 + y^2}",
    "\\int_0^1 x^2 dx",
    "\\sum_{i=1}^n i^2"
)

@KoinViewModel
class LatexViewModel : ViewModel() {
    val state = MutableStateFlow(emptyList<MTMathList>())

    fun parseLatex() {
        viewModelScope.launch(Dispatchers.Default) {
            state.value = latexFormulas.mapNotNull { latex ->
                if (latex.isNotEmpty()) {
                    val newParseError = MTParseError()
                    val list = MTMathListBuilder.buildFromString(latex, newParseError)
                    if (newParseError.errorCode != MTParseErrors.ErrorNone) {
                        null
                    } else {
                        list
                    }
                } else {
                    null
                }
            }
        }
    }
}
```

```kotlin
@Composable
fun LatexList(
    viewModel: LatexViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.parseLatex()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(modifier = modifier) {
        items(state) { mathList ->
            MTMathView(
                mathList = mathList,
                fontSize = 20.sp,
                textColor = Color.Black,
                font = null,
                mode = MTMathViewMode.KMTMathViewModeDisplay,
                textAlignment = MTTextAlignment.KMTTextAlignmentLeft,
                displayErrorInline = false,
                errorFontSize = 20.sp,
            )
        }
    }
}
```

## 自行构建

```shell script
# 构建iOS产物
# 下载freetype库到external目录
git submodule update --init --recursive
cd external/freetype
# 构建 FreeType 库
./build-ios-cmake.sh
```

### CI/CD 配置

对于分发不同平台的JVM应用，通过在不同平台（Windows/macOS/Linux）上构建来自动选择合适的本地库。

## 致谢

本项目基于 [AndroidMath](https://github.com/gregcockroft/AndroidMath) 项目开发，感谢原作者的贡献。

## 贡献

欢迎提交 Issue 和 Pull Request 来帮助改进这个项目。

## 联系方式

如有问题或建议，请通过 GitHub Issues 联系我们。
