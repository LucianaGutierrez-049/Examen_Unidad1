package pe.upeu.andinasalud.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion

@Composable
fun EstadoCitaChip(estado: EstadoCita) {
    val texto = when (estado) {
        is EstadoCita.Programada -> "Programada"
        is EstadoCita.Atendida -> "Atendida"
        is EstadoCita.Cancelada -> "Cancelada"
    }
    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(50)) {
        Text(texto, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelMedium)
    }
}

fun fechaLegible(cita: Cita): String {
    val fecha = cita.fechaHora
    val hora = fecha.hour.toString().padStart(2, '0')
    val minuto = fecha.minute.toString().padStart(2, '0')
    return "${fecha.date} · $hora:$minuto"
}

@Composable
fun CitaCard(cita: Cita, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(cita.medico.especialidad, style = MaterialTheme.typography.titleMedium)
            Text(cita.medico.nombre, style = MaterialTheme.typography.bodyMedium)
            Text("${cita.sede.nombre} · ${fechaLegible(cita)}", style = MaterialTheme.typography.bodyMedium)
            Text(if (cita.modalidad == ModalidadAtencion.PRESENCIAL) "🏥 Presencial" else "📹 Teleconsulta",
                style = MaterialTheme.typography.bodyMedium)
            EstadoCitaChip(cita.estado)
        }
    }
}

@Composable fun LoadingView() { Box(Modifier.fillMaxWidth().padding(32.dp)) { CircularProgressIndicator() } }
@Composable fun EmptyView(mensaje: String) { Text(mensaje, Modifier.padding(24.dp), style = MaterialTheme.typography.bodyLarge) }
@Composable fun ErrorView(mensaje: String, reintentar: () -> Unit) {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(mensaje, color = MaterialTheme.colorScheme.error)
        OutlinedButton(onClick = reintentar) { Text("Reintentar") }
    }
}

@Composable
fun CampoFormulario(etiqueta: String, valor: String, error: String?, onChange: (String) -> Unit,
    modifier: Modifier = Modifier, placeholder: String = "", minLineas: Int = 1) {
    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(value = valor, onValueChange = onChange, label = { Text(etiqueta) },
            placeholder = { Text(placeholder) }, isError = error != null, minLines = minLineas,
            modifier = Modifier.fillMaxWidth())
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
    }
}
