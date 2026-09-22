package pe.upeu.andinasalud

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.GlobalContext
import pe.upeu.andinasalud.di.iniciarKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    if (GlobalContext.getOrNull() == null) iniciarKoin()
    return ComposeUIViewController { App() }
}
