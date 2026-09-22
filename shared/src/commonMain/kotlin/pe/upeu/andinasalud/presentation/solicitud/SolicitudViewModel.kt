package pe.upeu.andinasalud.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.upeu.andinasalud.domain.model.NuevaCita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CampoSolicitud
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitudNoValida
import pe.upeu.andinasalud.presentation.common.LoadState

class SolicitudViewModel(private val repository: CitaRepository, private val solicitar: SolicitarCitaUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState
    init { cargar() }
    fun reiniciar() { _uiState.value = SolicitudUiState(); cargar() }
    fun cargar() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(carga = LoadState.Cargando)
        runCatching {
            delay(800)
            repository.obtenerEspecialidades() to repository.obtenerSedes()
        }.onSuccess { (especialidades, sedes) ->
            _uiState.value = _uiState.value.copy(especialidades = especialidades, sedes = sedes,
                carga = if (especialidades.isEmpty() || sedes.isEmpty()) LoadState.Vacio else LoadState.Contenido(Unit))
        }.onFailure { _uiState.value = _uiState.value.copy(carga = LoadState.Error(it.message ?: "No se pudo cargar el formulario")) }
    }
    fun especialidad(valor: String) { _uiState.value = _uiState.value.copy(especialidad = valor, errores = _uiState.value.errores.copy(especialidad = null)) }
    fun sede(valor: String) { _uiState.value = _uiState.value.copy(sedeId = valor, errores = _uiState.value.errores.copy(sede = null)) }
    fun fecha(valor: String) { _uiState.value = _uiState.value.copy(fecha = valor, errores = _uiState.value.errores.copy(fecha = null)) }
    fun hora(valor: String) { _uiState.value = _uiState.value.copy(hora = valor, errores = _uiState.value.errores.copy(hora = null)) }
    fun motivo(valor: String) { _uiState.value = _uiState.value.copy(motivo = valor, errores = _uiState.value.errores.copy(motivo = null)) }

    fun enviar() = viewModelScope.launch {
        val actual = _uiState.value
        val fecha = runCatching { LocalDate.parse(actual.fecha) }.getOrNull()
        val hora = runCatching { LocalTime.parse(actual.hora) }.getOrNull()
        val errores = ErroresFormulario(
            especialidad = if (actual.especialidad.isBlank()) "Selecciona una especialidad" else null,
            sede = if (actual.sedeId.isBlank()) "Selecciona una sede" else null,
            fecha = if (fecha == null) "Usa el formato AAAA-MM-DD" else null,
            hora = if (hora == null) "Usa el formato HH:MM" else null,
            motivo = if (actual.motivo.isBlank()) "Ingresa el motivo" else null,
        )
        if (errores != ErroresFormulario()) { _uiState.value = actual.copy(errores = errores); return@launch }
        _uiState.value = actual.copy(guardando = true)
        val paciente = repository.obtenerPaciente()
        solicitar(NuevaCita(paciente.id, actual.especialidad, actual.sedeId, LocalDateTime(fecha!!, hora!!), actual.motivo))
            .onSuccess { _uiState.value = _uiState.value.copy(guardando = false, guardada = true) }
            .onFailure { ex ->
                val mensaje = ex.message ?: "No se pudo solicitar la cita"
                val previos = _uiState.value.errores
                val nuevos = when ((ex as? SolicitudNoValida)?.campo) {
                    CampoSolicitud.ESPECIALIDAD -> previos.copy(especialidad = mensaje)
                    CampoSolicitud.SEDE -> previos.copy(sede = mensaje)
                    CampoSolicitud.FECHA -> previos.copy(fecha = mensaje)
                    CampoSolicitud.HORA -> previos.copy(hora = mensaje)
                    else -> previos.copy(motivo = mensaje)
                }
                _uiState.value = _uiState.value.copy(guardando = false, errores = nuevos)
            }
    }
}
