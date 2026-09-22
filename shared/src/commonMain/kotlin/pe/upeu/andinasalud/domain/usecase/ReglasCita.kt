package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class ReglasCita(private val clock: Clock = Clock.System, private val zona: TimeZone = TimeZone.currentSystemDefault()) {
    fun validarFecha(fechaHora: LocalDateTime): String? =
        if (fechaHora.toInstant(zona) <= clock.now()) "Elige una fecha y hora futuras" else null

    fun validarMotivo(motivo: String): String? =
        if (motivo.trim().length !in 10..200) "El motivo debe tener entre 10 y 200 caracteres" else null

    fun validarCupo(citas: List<Cita>, pacienteId: String): String? =
        if (citas.count { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada } >= 3)
            "Ya tienes tres citas programadas" else null

    fun validarHorario(citas: List<Cita>, pacienteId: String, fechaHora: LocalDateTime): String? =
        if (citas.any { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada && it.fechaHora == fechaHora })
            "Ya tienes una cita programada en ese horario" else null

    fun puedeCancelar(cita: Cita): Boolean =
        cita.estado is EstadoCita.Programada && cita.fechaHora.toInstant(zona) - clock.now() > 24.hours
}
