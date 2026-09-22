package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class ReglasCita(private val clock: Clock = Clock.System, private val zona: TimeZone = TimeZone.currentSystemDefault()) {
    companion object { const val LIMITE_PROGRAMADAS = 3 }
    fun validarFecha(fechaHora: LocalDateTime): String? =
        if (fechaHora.toInstant(zona) <= clock.now()) "Elige una fecha y hora futuras" else null

    fun validarMotivo(motivo: String): String? =
        if (motivo.trim().length !in 10..200) "El motivo debe tener entre 10 y 200 caracteres" else null

    fun contarProgramadas(citas: List<Cita>, pacienteId: String): Int =
        citas.count { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada }

    fun puedeSolicitar(citas: List<Cita>, pacienteId: String): Boolean =
        contarProgramadas(citas, pacienteId) < LIMITE_PROGRAMADAS

    fun validarCupo(citas: List<Cita>, pacienteId: String): String? =
        if (!puedeSolicitar(citas, pacienteId))
            "Ya tienes $LIMITE_PROGRAMADAS citas programadas" else null

    fun mismoHorario(primera: LocalDateTime, segunda: LocalDateTime): Boolean =
        primera.date == segunda.date && primera.hour == segunda.hour && primera.minute == segunda.minute

    fun validarHorario(citas: List<Cita>, pacienteId: String, fechaHora: LocalDateTime, exceptoId: String? = null): String? =
        if (citas.any { it.id != exceptoId && it.pacienteId == pacienteId &&
                it.estado is EstadoCita.Programada && mismoHorario(it.fechaHora, fechaHora) })
            "Ya tienes una cita programada en ese horario" else null

    fun puedeCancelar(cita: Cita): Boolean =
        cita.estado is EstadoCita.Programada && cita.fechaHora.toInstant(zona) - clock.now() > 24.hours
}
