package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.*

@Composable
fun CitasScreen(estado: CitasUiState, buscar: (String) -> Unit, filtrar: (FiltroEstado) -> Unit,
    recargar: () -> Unit, abrirDetalle: (String) -> Unit, solicitar: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mis citas", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = solicitar) { Text("+ Solicitar") }
        }
        OutlinedTextField(estado.busqueda, buscar, label = { Text("Buscar especialidad o médico") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FiltroEstado.entries.forEach { filtro ->
                FilterChip(selected = estado.filtro == filtro, onClick = { filtrar(filtro) },
                    label = { Text(when (filtro) {
                        FiltroEstado.TODAS -> "Todas"
                        FiltroEstado.PROGRAMADAS -> "Programadas"
                        FiltroEstado.ATENDIDAS -> "Atendidas"
                        FiltroEstado.CANCELADAS -> "Canceladas"
                    }) })
            }
        }
        when (val carga = estado.carga) {
            LoadState.Cargando -> LoadingView()
            LoadState.Vacio -> EmptyView("No hay citas con estos filtros")
            is LoadState.Error -> ErrorView(carga.mensaje, recargar)
            is LoadState.Contenido -> LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(carga.datos, key = { it.id }) { CitaCard(it, { abrirDetalle(it.id) }) }
            }
        }
    }
}
