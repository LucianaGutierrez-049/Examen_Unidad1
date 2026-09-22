package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.presentation.common.LoadState

class CitasViewModel(private val obtener: ObtenerCitasUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState: StateFlow<CitasUiState> = _uiState
    private var todas = emptyList<Cita>()
    fun cargar() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(carga = LoadState.Cargando)
        runCatching { obtener() }.onSuccess { todas = it; filtrar(forzar = true) }
            .onFailure {
                if (it is CancellationException) throw it
                _uiState.value = _uiState.value.copy(carga = LoadState.Error(it.message ?: "No se pudieron cargar las citas"))
            }
    }
    fun buscar(texto: String) { _uiState.value = _uiState.value.copy(busqueda = texto); filtrar() }
    fun seleccionarFiltro(filtro: FiltroEstado) { _uiState.value = _uiState.value.copy(filtro = filtro); filtrar() }

    private fun filtrar(forzar: Boolean = false) {
        val estado = _uiState.value
        if (!forzar && (estado.carga is LoadState.Cargando || estado.carga is LoadState.Error)) return
        val consulta = normalizar(estado.busqueda.trim())
        val resultado = todas.filter { cita ->
            (when (estado.filtro) {
                FiltroEstado.TODAS -> true
                FiltroEstado.PROGRAMADAS -> cita.estado is EstadoCita.Programada
                FiltroEstado.ATENDIDAS -> cita.estado is EstadoCita.Atendida
                FiltroEstado.CANCELADAS -> cita.estado is EstadoCita.Cancelada
            }) && (consulta.isEmpty() || normalizar(cita.medico.especialidad).contains(consulta) || normalizar(cita.medico.nombre).contains(consulta))
        }
        _uiState.value = estado.copy(carga = if (resultado.isEmpty()) LoadState.Vacio else LoadState.Contenido(resultado))
    }
}

internal fun normalizar(texto: String): String = texto.lowercase().map { caracter ->
    when (caracter) {
        'á', 'à', 'ä' -> 'a'
        'é', 'è', 'ë' -> 'e'
        'í', 'ì', 'ï' -> 'i'
        'ó', 'ò', 'ö' -> 'o'
        'ú', 'ù', 'ü' -> 'u'
        else -> caracter
    }
}.joinToString("")
