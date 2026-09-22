package pe.upeu.andinasalud.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.presentation.common.LoadState

class PerfilViewModel(private val repository: CitaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<LoadState<Paciente>>(LoadState.Cargando)
    val uiState: StateFlow<LoadState<Paciente>> = _uiState
    init { cargar() }
    fun cargar() = viewModelScope.launch {
        _uiState.value = LoadState.Cargando
        runCatching { delay(800); repository.obtenerPaciente() }
            .onSuccess { _uiState.value = if (it.nombre.isBlank()) LoadState.Vacio else LoadState.Contenido(it) }
            .onFailure { _uiState.value = LoadState.Error(it.message ?: "No se pudo cargar el perfil") }
    }
}
