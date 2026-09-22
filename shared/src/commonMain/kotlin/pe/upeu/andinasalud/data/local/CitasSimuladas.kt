package pe.upeu.andinasalud.data.local

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.upeu.andinasalud.domain.model.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

object CitasSimuladas {
    val paciente = Paciente("P-0417", "Lucía Gutiérrez", "70154823", "lucia@correo.pe", "987 654 321")
    val sedes = listOf(Sede("nana", "Ñaña"), Sede("chosica", "Chosica"),
        Sede("chaclacayo", "Chaclacayo"), Sede("santa-anita", "Santa Anita"))
    val especialidades = listOf("Medicina General", "Odontología", "Pediatría", "Nutrición", "Psicología")
    val medicos = listOf(
        Medico("M01", "Dr. Iván Rojas", especialidades[0], listOf(sedes[0], sedes[1])),
        Medico("M02", "Dra. Elena Salazar", especialidades[0], listOf(sedes[2], sedes[3])),
        Medico("M03", "Dra. Rosa Flores", especialidades[1], listOf(sedes[1], sedes[3])),
        Medico("M04", "Dr. Diego Huamán", especialidades[1], listOf(sedes[0], sedes[2])),
        Medico("M05", "Dra. Carla Núñez", especialidades[2], listOf(sedes[2], sedes[3])),
        Medico("M06", "Dr. Marco Quispe", especialidades[2], listOf(sedes[0], sedes[1])),
        Medico("M07", "Lic. Ana Bermúdez", especialidades[3], listOf(sedes[3], sedes[0])),
        Medico("M08", "Lic. José Paredes", especialidades[3], listOf(sedes[1], sedes[2])),
        Medico("M09", "Ps. Luis Tapia", especialidades[4], listOf(sedes[0], sedes[2])),
        Medico("M10", "Ps. María Torres", especialidades[4], listOf(sedes[1], sedes[3])),
    )

    fun citasIniciales(): List<Cita> {
        val zona = TimeZone.currentSystemDefault()
        fun fecha(dias: Int) = (Clock.System.now() + dias.days).toLocalDateTime(zona)
        return listOf(
            Cita("C-1", paciente.id, medicos[0], sedes[0], fecha(4), "Control médico general", EstadoCita.Programada(true), ModalidadAtencion.PRESENCIAL),
            Cita("C-2", paciente.id, medicos[2], sedes[1], fecha(8), "Evaluación odontológica", EstadoCita.Programada(false), ModalidadAtencion.PRESENCIAL),
            Cita("C-3", paciente.id, medicos[6], sedes[3], fecha(13), "Consulta de nutrición", EstadoCita.Programada(true), ModalidadAtencion.TELECONSULTA),
            Cita("C-4", paciente.id, medicos[4], sedes[2], fecha(-30), "Control pediátrico", EstadoCita.Atendida("Control en tres meses"), ModalidadAtencion.PRESENCIAL),
            Cita("C-5", paciente.id, medicos[8], sedes[0], fecha(-15), "Seguimiento psicológico", EstadoCita.Atendida("Continuar sesiones quincenales"), ModalidadAtencion.TELECONSULTA),
            Cita("C-6", paciente.id, medicos[0], sedes[1], fecha(-10), "Consulta general", EstadoCita.Cancelada("Viaje del paciente", true), ModalidadAtencion.PRESENCIAL),
        )
    }
}
