package io.github.darriousliu.katex

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.darriousliu.katex.latex.LatexScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.module

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(Module.module)
        }
    ) {
        MaterialTheme {
            LatexScreen()
        }
    }
}