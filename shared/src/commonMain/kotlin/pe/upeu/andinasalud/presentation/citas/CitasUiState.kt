package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.presentation.common.LoadState
import pe.upeu.andinasalud.domain.usecase.ReglasCita

enum class FiltroEstado { TODAS, PROGRAMADAS, ATENDIDAS, CANCELADAS }
data class CitasUiState(
    val carga: LoadState<List<Cita>> = LoadState.Cargando,
    val busqueda: String = "",
    val filtro: FiltroEstado = FiltroEstado.TODAS,
    val soloHoy: Boolean = false,
    val programadas: Int? = null,
    val limiteProgramadas: Int = ReglasCita.LIMITE_PROGRAMADAS,
    val puedeSolicitar: Boolean = false,
)
