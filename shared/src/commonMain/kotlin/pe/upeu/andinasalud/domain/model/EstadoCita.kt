package pe.upeu.andinasalud.domain.model

sealed interface EstadoCita {
    data class Programada(val recordatorioActivo: Boolean) : EstadoCita
    data class Atendida(val indicaciones: String) : EstadoCita
    data class Cancelada(val motivo: String, val canceladaPorPaciente: Boolean) : EstadoCita
}
