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
    val modalidad: ModalidadAtencion,
    val cambiosProgramacion: List<CambioProgramacion> = emptyList(),
)

data class CambioProgramacion(val anterior: LocalDateTime, val nueva: LocalDateTime)

data class NuevaCita(
    val pacienteId: String,
    val especialidad: String,
    val sedeId: String,
    val fechaHora: LocalDateTime,
    val motivo: String,
    val modalidad: ModalidadAtencion,
)
