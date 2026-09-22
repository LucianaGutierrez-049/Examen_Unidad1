package pe.upeu.andinasalud.presentation.solicitud

import pe.upeu.andinasalud.domain.model.Sede
import pe.upeu.andinasalud.presentation.common.LoadState

data class ErroresFormulario(val especialidad: String? = null, val sede: String? = null,
    val fecha: String? = null, val hora: String? = null, val motivo: String? = null)
data class SolicitudUiState(
    val carga: LoadState<Unit> = LoadState.Cargando,
    val especialidades: List<String> = emptyList(),
    val sedes: List<Sede> = emptyList(),
    val especialidad: String = "",
    val sedeId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val motivo: String = "",
    val errores: ErroresFormulario = ErroresFormulario(),
    val guardando: Boolean = false,
    val guardada: Boolean = false,
)
