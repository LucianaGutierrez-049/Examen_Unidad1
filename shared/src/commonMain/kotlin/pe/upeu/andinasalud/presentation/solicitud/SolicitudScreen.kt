package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.*

@Composable
fun SolicitudScreen(estado: SolicitudUiState, volver: () -> Unit, verCitas: () -> Unit, recargar: () -> Unit,
    especialidad: (String) -> Unit, sede: (String) -> Unit, fecha: (String) -> Unit,
    hora: (String) -> Unit, motivo: (String) -> Unit, enviar: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TextButton(onClick = volver) { Text("‹ Volver") }
        Text("Solicitar cita", style = MaterialTheme.typography.headlineMedium)
        when (val carga = estado.carga) {
            LoadState.Cargando -> LoadingView()
            LoadState.Vacio -> EmptyView("No hay especialidades o sedes disponibles")
            is LoadState.Error -> ErrorView(carga.mensaje, recargar)
            is LoadState.Contenido -> if (estado.guardada) {
                Text("Tu cita fue solicitada correctamente", style = MaterialTheme.typography.titleMedium)
                Button(onClick = verCitas) { Text("Ver mis citas") }
            } else {
                Selector("Especialidad", estado.especialidad, estado.especialidades.map { it to it },
                    estado.errores.especialidad, especialidad)
                Selector("Sede", estado.sedes.firstOrNull { it.id == estado.sedeId }?.nombre.orEmpty(),
                    estado.sedes.map { it.id to it.nombre }, estado.errores.sede, sede)
                CampoFormulario("Fecha", estado.fecha, estado.errores.fecha, fecha, placeholder = "AAAA-MM-DD")
                CampoFormulario("Hora", estado.hora, estado.errores.hora, hora, placeholder = "HH:MM")
                CampoFormulario("Motivo de consulta", estado.motivo, estado.errores.motivo, motivo, minLineas = 3)
                estado.envioError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(onClick = enviar, enabled = !estado.guardando, modifier = Modifier.fillMaxWidth()) {
                    Text(if (estado.guardando) "Guardando…" else "Confirmar solicitud")
                }
            }
        }
    }
}

@Composable
private fun Selector(etiqueta: String, valor: String, opciones: List<Pair<String, String>>,
    error: String?, seleccionar: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    Column {
        OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$etiqueta: ${valor.ifBlank { "Seleccionar" }}")
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { (id, nombre) -> DropdownMenuItem(text = { Text(nombre) },
                onClick = { seleccionar(id); abierto = false }) }
        }
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
    }
}
