package io.github.darriousliu.katex.core

import android.content.Context
import androidx.startup.Initializer
import io.github.darriousliu.katex.utils.appContext

class AppContextInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        appContext = context.applicationContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}