package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.CancellationException
import kotlinx.datetime.LocalDateTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ReprogramarCitaUseCase(private val repository: CitaRepository, private val reglas: ReglasCita,
    private val guard: OperacionCitaGuard) {
    suspend operator fun invoke(id: String, nuevaFechaHora: LocalDateTime): Result<Cita> = try {
        Result.success(guard.ejecutar {
            val cita = repository.obtenerCita(id) ?: error("La cita no existe")
            if (cita.estado !is EstadoCita.Programada) error("Solo se puede reprogramar una cita programada")
            reglas.validarFecha(nuevaFechaHora)?.let { throw SolicitudNoValida(CampoSolicitud.FECHA, it) }
            if (reglas.mismoHorario(nuevaFechaHora, cita.fechaHora))
                throw SolicitudNoValida(CampoSolicitud.HORA, "Selecciona un horario diferente")
            val citas = repository.obtenerCitas()
            reglas.validarHorario(citas, cita.pacienteId, nuevaFechaHora, id)?.let {
                throw SolicitudNoValida(CampoSolicitud.HORA, it)
            }
            repository.reprogramarCita(id, nuevaFechaHora)
        })
    } catch (cancelada: CancellationException) {
        throw cancelada
    } catch (error: Exception) {
        Result.failure(error)
    }
}
