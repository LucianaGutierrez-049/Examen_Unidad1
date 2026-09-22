package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCitasUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke(): List<Cita> = repository.obtenerCitas().sortedBy { it.fechaHora }
}
