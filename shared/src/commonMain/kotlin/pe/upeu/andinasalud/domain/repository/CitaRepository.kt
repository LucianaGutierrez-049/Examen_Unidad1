package pe.upeu.andinasalud.domain.repository

import pe.upeu.andinasalud.domain.model.*

interface CitaRepository {
    suspend fun obtenerPaciente(): Paciente
    suspend fun obtenerSedes(): List<Sede>
    suspend fun obtenerEspecialidades(): List<String>
    suspend fun obtenerMedicos(): List<Medico>
    suspend fun obtenerCitas(): List<Cita>
    suspend fun obtenerCita(id: String): Cita?
    suspend fun solicitarCita(cita: Cita): Cita
    suspend fun cancelarCita(id: String, motivo: String): Cita
}
