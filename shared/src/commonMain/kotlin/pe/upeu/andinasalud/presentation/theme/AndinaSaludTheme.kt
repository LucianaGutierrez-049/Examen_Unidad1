package pe.upeu.andinasalud.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val esquemaClaro = lightColorScheme(primary = AzulAndino, secondary = VerdeSalud,
    background = FondoClaro, surface = Color.White, primaryContainer = Color(0xFFD5EDF1),
    onPrimaryContainer = Color(0xFF103D4B))
private val esquemaOscuro = darkColorScheme(primary = Color(0xFF94D7E7), secondary = Color(0xFF7BD5C6),
    background = FondoOscuro, surface = Color(0xFF1C2A32), primaryContainer = Color(0xFF164657),
    onPrimaryContainer = Color(0xFFD6F2F6))

@Composable
fun AndinaSaludTheme(oscuro: Boolean, contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (oscuro) esquemaOscuro else esquemaClaro, typography = AndinaTypography, content = contenido)
}
