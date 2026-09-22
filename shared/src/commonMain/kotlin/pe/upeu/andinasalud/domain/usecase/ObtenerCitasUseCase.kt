package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import kotlin.time.Clock

class ObtenerCitasUseCase(private val repository: CitaRepository,
    private val clock: Clock = Clock.System, private val zona: TimeZone = TimeZone.currentSystemDefault()) {
    suspend operator fun invoke(): List<Cita> {
        val ahora = clock.now()
        val (futuras, pasadas) = repository.obtenerCitas().partition { it.fechaHora.toInstant(zona) >= ahora }
        return futuras.sortedBy { it.fechaHora } + pasadas.sortedByDescending { it.fechaHora }
    }

    suspend fun proximaProgramada(): Cita? = invoke().firstOrNull {
        it.estado is EstadoCita.Programada && it.fechaHora.toInstant(zona) > clock.now()
    }
}
