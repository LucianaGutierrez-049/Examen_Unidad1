package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.CancellationException
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CancelarCitaUseCase(private val repository: CitaRepository, private val reglas: ReglasCita,
    private val guard: OperacionCitaGuard) {
    suspend operator fun invoke(id: String): Result<Cita> = try {
        Result.success(guard.ejecutar {
            val cita = repository.obtenerCita(id) ?: error("La cita no existe")
            if (!reglas.puedeCancelar(cita)) error("Solo se puede cancelar una cita programada con más de 24 horas de anticipación")
            repository.cancelarCita(id, "Cancelada por el paciente")
        })
    } catch (cancelada: CancellationException) {
        throw cancelada
    } catch (error: Exception) {
        Result.failure(error)
    }
}
