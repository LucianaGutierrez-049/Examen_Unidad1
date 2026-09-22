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
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitudNoValida
import pe.upeu.andinasalud.domain.usecase.CampoSolicitud
import pe.upeu.andinasalud.presentation.common.LoadState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

data class DetalleUiState(val carga: LoadState<Cita> = LoadState.Cargando, val puedeCancelar: Boolean = false,
    val accionError: String? = null, val reprogramando: Boolean = false, val fecha: String = "",
    val hora: String = "", val errorFecha: String? = null, val errorHora: String? = null,
    val guardando: Boolean = false)

class DetalleCitaViewModel(private val repository: CitaRepository, private val cancelar: CancelarCitaUseCase,
    private val reglas: ReglasCita, private val reprogramar: ReprogramarCitaUseCase) : ViewModel() {
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
    fun alternarReprogramacion() {
        _uiState.value = _uiState.value.copy(reprogramando = !_uiState.value.reprogramando,
            fecha = "", hora = "", errorFecha = null, errorHora = null, accionError = null)
    }
    fun fecha(valor: String) { _uiState.value = _uiState.value.copy(fecha = valor, errorFecha = null) }
    fun hora(valor: String) { _uiState.value = _uiState.value.copy(hora = valor, errorHora = null) }

    fun confirmarReprogramacion(id: String) = viewModelScope.launch {
        val actual = _uiState.value
        if (actual.guardando) return@launch
        val nuevaFecha = runCatching { LocalDate.parse(actual.fecha) }.getOrNull()
        val nuevaHora = runCatching { LocalTime.parse(actual.hora) }.getOrNull()
        if (nuevaFecha == null || nuevaHora == null) {
            _uiState.value = actual.copy(errorFecha = if (nuevaFecha == null) "Usa el formato AAAA-MM-DD" else null,
                errorHora = if (nuevaHora == null) "Usa el formato HH:MM" else null)
            return@launch
        }
        _uiState.value = actual.copy(guardando = true, accionError = null)
        reprogramar(id, LocalDateTime(nuevaFecha, nuevaHora))
            .onSuccess { cita ->
                _uiState.value = DetalleUiState(LoadState.Contenido(cita), reglas.puedeCancelar(cita))
            }
            .onFailure { error ->
                _uiState.value = if (error is SolicitudNoValida) {
                    when (error.campo) {
                        CampoSolicitud.FECHA -> _uiState.value.copy(guardando = false, errorFecha = error.message)
                        CampoSolicitud.HORA -> _uiState.value.copy(guardando = false, errorHora = error.message)
                        else -> _uiState.value.copy(guardando = false, accionError = error.message)
                    }
                } else _uiState.value.copy(guardando = false,
                    accionError = error.message ?: "No se pudo reprogramar la cita")
            }
    }
}
