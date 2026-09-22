package pe.upeu.andinasalud.domain.model

import kotlinx.datetime.LocalDateTime

data class Cita(
    val id: String,
    val pacienteId: String,
    val medico: Medico,
    val sede: Sede,
    val fechaHora: LocalDateTime,
    val motivo: String,
    val estado: EstadoCita,
)

data class NuevaCita(
    val pacienteId: String,
    val especialidad: String,
    val sedeId: String,
    val fechaHora: LocalDateTime,
    val motivo: String,
)
