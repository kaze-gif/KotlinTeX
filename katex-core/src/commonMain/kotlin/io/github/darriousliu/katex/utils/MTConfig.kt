package io.github.darriousliu.katex.utils

object MTConfig {
    internal var IS_DEBUG = false

    /**
     * 是否开启调试模式
     * 开启后会在渲染时绘制字形的边框
     */
    fun setDebugMode(debug: Boolean) {
        IS_DEBUG = debug
    }
}