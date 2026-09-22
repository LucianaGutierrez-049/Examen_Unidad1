package pe.upeu.andinasalud.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.presentation.common.LoadState

data class InicioDatos(val paciente: Paciente, val proxima: Cita?)

class InicioViewModel(private val repository: CitaRepository, private val obtener: ObtenerCitasUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<LoadState<InicioDatos>>(LoadState.Cargando)
    val uiState: StateFlow<LoadState<InicioDatos>> = _uiState
    init { cargar() }
    fun cargar() = viewModelScope.launch {
        _uiState.value = LoadState.Cargando
        runCatching {
            val paciente = repository.obtenerPaciente()
            val proxima = obtener().firstOrNull { it.estado is EstadoCita.Programada }
            InicioDatos(paciente, proxima)
        }.onSuccess { _uiState.value = if (it.paciente.nombre.isBlank()) LoadState.Vacio else LoadState.Contenido(it) }
            .onFailure { _uiState.value = LoadState.Error(it.message ?: "No se pudo cargar el inicio") }
    }
}
