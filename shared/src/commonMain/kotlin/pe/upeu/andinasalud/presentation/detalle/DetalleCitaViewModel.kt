package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ReglasCita
import pe.upeu.andinasalud.presentation.common.LoadState

data class DetalleUiState(val carga: LoadState<Cita> = LoadState.Cargando, val puedeCancelar: Boolean = false, val accionError: String? = null)

class DetalleCitaViewModel(private val repository: CitaRepository, private val cancelar: CancelarCitaUseCase,
    private val reglas: ReglasCita) : ViewModel() {
    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState
    fun cargar(id: String) = viewModelScope.launch {
        _uiState.value = DetalleUiState()
        runCatching { repository.obtenerCita(id) }.onSuccess { cita ->
            _uiState.value = if (cita == null) DetalleUiState(LoadState.Vacio)
                else DetalleUiState(LoadState.Contenido(cita), reglas.puedeCancelar(cita))
        }.onFailure {
            if (it is CancellationException) throw it
            _uiState.value = DetalleUiState(LoadState.Error(it.message ?: "No se pudo cargar la cita"))
        }
    }
    fun cancelar(id: String) = viewModelScope.launch {
        cancelar.invoke(id).onSuccess { _uiState.value = DetalleUiState(LoadState.Contenido(it), false) }
            .onFailure { _uiState.value = _uiState.value.copy(accionError = it.message ?: "No se pudo cancelar") }
    }
}
