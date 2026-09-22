package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.presentation.common.*

@Composable
fun DetalleCitaScreen(estado: DetalleUiState, volver: () -> Unit, recargar: () -> Unit, cancelar: () -> Unit,
    iniciarReprogramacion: () -> Unit, fecha: (String) -> Unit, hora: (String) -> Unit,
    confirmarReprogramacion: () -> Unit) {
    var confirmar by remember { mutableStateOf(false) }
    if (confirmar) AlertDialog(onDismissRequest = { confirmar = false }, title = { Text("Cancelar cita") },
        text = { Text("¿Confirmas que deseas cancelar esta cita?") },
        confirmButton = { TextButton(onClick = { confirmar = false; cancelar() }) { Text("Sí, cancelar") } },
        dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Volver") } })
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TextButton(onClick = volver) { Text("‹ Volver") }
        Text("Detalle de cita", style = MaterialTheme.typography.headlineMedium)
        when (val carga = estado.carga) {
            LoadState.Cargando -> LoadingView()
            LoadState.Vacio -> EmptyView("No se encontró la cita")
            is LoadState.Error -> ErrorView(carga.mensaje, recargar)
            is LoadState.Contenido -> {
                val cita = carga.datos
                DetailRow("Especialidad", cita.medico.especialidad)
                DetailRow("Médico", cita.medico.nombre)
                DetailRow("Sede", cita.sede.nombre)
                DetailRow("Fecha y hora", fechaLegible(cita))
                DetailRow("Modalidad", if (cita.modalidad == ModalidadAtencion.PRESENCIAL) "🏥 Presencial" else "📹 Teleconsulta")
                DetailRow("Motivo", cita.motivo)
                cita.cambiosProgramacion.forEachIndexed { indice, cambio ->
                    DetailRow("Reprogramación ${indice + 1}", "${cambio.anterior} → ${cambio.nueva}")
                }
                EstadoCitaChip(cita.estado)
                when (val estadoCita = cita.estado) {
                    is EstadoCita.Atendida -> DetailRow("Indicaciones", estadoCita.indicaciones)
                    is EstadoCita.Cancelada -> DetailRow("Motivo de cancelación", estadoCita.motivo)
                    is EstadoCita.Programada -> Unit
                }
                if (estado.puedeCancelar) Button(onClick = { confirmar = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar cita")
                }
                if (cita.estado is EstadoCita.Programada) {
                    if (estado.reprogramando) {
                        Text("Nueva fecha y hora", style = MaterialTheme.typography.titleMedium)
                        CampoFormulario("Fecha", estado.fecha, estado.errorFecha, fecha, placeholder = "AAAA-MM-DD")
                        CampoFormulario("Hora", estado.hora, estado.errorHora, hora, placeholder = "HH:MM")
                        Button(onClick = confirmarReprogramacion, enabled = !estado.guardando,
                            modifier = Modifier.fillMaxWidth()) {
                            Text(if (estado.guardando) "Guardando…" else "Confirmar reprogramación")
                        }
                        TextButton(onClick = iniciarReprogramacion) { Text("Cerrar formulario") }
                    } else {
                        OutlinedButton(onClick = iniciarReprogramacion, modifier = Modifier.fillMaxWidth()) {
                            Text("Reprogramar cita")
                        }
                    }
                }
                estado.accionError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable private fun DetailRow(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
