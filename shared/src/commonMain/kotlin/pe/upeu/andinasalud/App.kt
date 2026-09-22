package pe.upeu.andinasalud

import androidx.compose.runtime.*
import pe.upeu.andinasalud.presentation.navigation.AppNavHost
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

@Composable
fun App() {
    var oscuro by remember { mutableStateOf(false) }
    AndinaSaludTheme(oscuro) { AppNavHost(oscuro) { oscuro = it } }
}
