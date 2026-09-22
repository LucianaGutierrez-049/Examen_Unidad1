package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository

enum class CampoSolicitud { ESPECIALIDAD, SEDE, FECHA, HORA, MOTIVO }
class SolicitudNoValida(val campo: CampoSolicitud, message: String) : IllegalArgumentException(message)

class SolicitarCitaUseCase(private val repository: CitaRepository, private val reglas: ReglasCita) {
    suspend operator fun invoke(nueva: NuevaCita): Result<Cita> = runCatching {
        reglas.validarFecha(nueva.fechaHora)?.let { throw SolicitudNoValida(CampoSolicitud.FECHA, it) }
        reglas.validarMotivo(nueva.motivo)?.let { throw SolicitudNoValida(CampoSolicitud.MOTIVO, it) }
        val citas = repository.obtenerCitas()
        reglas.validarCupo(citas, nueva.pacienteId)?.let { throw SolicitudNoValida(CampoSolicitud.FECHA, it) }
        reglas.validarHorario(citas, nueva.pacienteId, nueva.fechaHora)?.let { throw SolicitudNoValida(CampoSolicitud.HORA, it) }
        val sede = repository.obtenerSedes().firstOrNull { it.id == nueva.sedeId }
            ?: throw SolicitudNoValida(CampoSolicitud.SEDE, "Selecciona una sede")
        val medico = repository.obtenerMedicos().firstOrNull {
            it.especialidad == nueva.especialidad && sede in it.sedes
        } ?: throw SolicitudNoValida(CampoSolicitud.SEDE, "No hay médicos para esta especialidad y sede")
        repository.solicitarCita(Cita("C-${citas.size + 1}", nueva.pacienteId, medico, sede,
            nueva.fechaHora, nueva.motivo.trim(), EstadoCita.Programada(true)))
    }
}
