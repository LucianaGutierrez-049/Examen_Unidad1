package pe.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.*

@Composable
fun InicioScreen(estado: LoadState<InicioDatos>, recargar: () -> Unit, irCitas: () -> Unit,
    solicitar: () -> Unit, abrirDetalle: (String) -> Unit, puedeSolicitar: Boolean) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("AndinaSalud", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        when (estado) {
            LoadState.Cargando -> LoadingView()
            LoadState.Vacio -> EmptyView("No hay datos disponibles")
            is LoadState.Error -> ErrorView(estado.mensaje, recargar)
            is LoadState.Contenido -> {
                Text("Hola, ${estado.datos.paciente.nombre.substringBefore(' ')}", style = MaterialTheme.typography.titleLarge)
                Text("Tu salud, cerca de ti", style = MaterialTheme.typography.bodyLarge)
                Text("Próxima cita", style = MaterialTheme.typography.titleMedium)
                estado.datos.proxima?.let { CitaCard(it, { abrirDetalle(it.id) }) }
                    ?: EmptyView("Todavía no tienes una próxima cita")
            }
        }
        Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium)
        Button(onClick = irCitas, modifier = Modifier.fillMaxWidth()) { Text("Mis citas") }
        OutlinedButton(onClick = solicitar, enabled = puedeSolicitar, modifier = Modifier.fillMaxWidth()) { Text("Solicitar cita") }
    }
}
