package pe.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.presentation.common.*

@Composable
fun PerfilScreen(estado: LoadState<Paciente>, recargar: () -> Unit, oscuro: Boolean, cambiarTema: (Boolean) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Perfil y ajustes", style = MaterialTheme.typography.headlineMedium)
        when (estado) {
            LoadState.Cargando -> LoadingView()
            LoadState.Vacio -> EmptyView("No hay datos de paciente")
            is LoadState.Error -> ErrorView(estado.mensaje, recargar)
            is LoadState.Contenido -> {
                Dato("Nombre", estado.datos.nombre)
                Dato("Documento", estado.datos.documento)
                Dato("Correo", estado.datos.correo)
                Dato("Teléfono", estado.datos.telefono)
            }
        }
        HorizontalDivider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tema oscuro", style = MaterialTheme.typography.titleMedium)
            Switch(checked = oscuro, onCheckedChange = cambiarTema)
        }
    }
}

@Composable private fun Dato(titulo: String, valor: String) {
    Column {
        Text(titulo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
