package pe.upeu.andinasalud

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.NuevaCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.OperacionCitaGuard
import pe.upeu.andinasalud.domain.usecase.ReglasCita
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.normalizar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.days

class ReglasCitaTest {
    private val reglas = ReglasCita()
    private val citas = CitasSimuladas.citasIniciales()

    @Test fun fechaPasadaYMotivoFueraDeRango() {
        val ayer = (Clock.System.now() - 25.hours).toLocalDateTime(TimeZone.currentSystemDefault())
        assertNotNull(reglas.validarFecha(ayer))
        assertNotNull(reglas.validarMotivo("corto"))
        assertNotNull(reglas.validarMotivo("x".repeat(201)))
        assertNull(reglas.validarMotivo("Control médico"))
    }

    @Test fun cupoYHorario() {
        assertNotNull(reglas.validarCupo(citas, CitasSimuladas.paciente.id))
        assertEquals(3, reglas.contarProgramadas(citas, CitasSimuladas.paciente.id))
        assertFalse(reglas.puedeSolicitar(citas, CitasSimuladas.paciente.id))
        assertNotNull(reglas.validarHorario(citas, CitasSimuladas.paciente.id, citas[0].fechaHora))
        val mismoMinuto = kotlinx.datetime.LocalDateTime(citas[0].fechaHora.date,
            kotlinx.datetime.LocalTime(citas[0].fechaHora.hour, citas[0].fechaHora.minute))
        assertNotNull(reglas.validarHorario(citas, CitasSimuladas.paciente.id, mismoMinuto))
        assertNull(reglas.validarHorario(citas, "otro-paciente", citas[0].fechaHora))
        assertNull(reglas.validarHorario(citas, CitasSimuladas.paciente.id, citas[0].fechaHora, citas[0].id))
    }

    @Test fun cancelacionSoloProgramadaConMasDeUnDia() {
        assertTrue(reglas.puedeCancelar(citas[0]))
        assertFalse(reglas.puedeCancelar(citas[3]))
        assertFalse(reglas.puedeCancelar(citas[5]))
        val cercana = citas[0].copy(fechaHora = (Clock.System.now() + 23.hours).toLocalDateTime(TimeZone.currentSystemDefault()))
        assertFalse(reglas.puedeCancelar(cercana))
    }

    @Test fun datosYBusquedaSinTildes() {
        assertEquals(3, citas.count { it.estado is EstadoCita.Programada })
        assertEquals(2, citas.count { it.estado is EstadoCita.Atendida })
        assertEquals(1, citas.count { it.estado is EstadoCita.Cancelada })
        assertEquals(10, CitasSimuladas.medicos.size)
        assertEquals("nutricion", normalizar("Nutrición"))
    }

    @Test fun listaPriorizaCitasFuturasYRepositorioRepresentaVacioYError() = runBlocking {
        val ordenadas = ObtenerCitasUseCase(CitaRepositoryFake(retardoCargaMs = 0))()
        assertEquals("C-1", ordenadas.first().id)
        assertEquals("C-6", ordenadas[3].id)
        assertTrue(CitaRepositoryFake(emptyList(), retardoCargaMs = 0).obtenerCitas().isEmpty())
        assertTrue(runCatching {
            CitaRepositoryFake(falloLectura = true, retardoCargaMs = 0).obtenerCitas()
        }.isFailure)
    }

    @Test fun cancelacionLiberaCupoYPermiteSolicitud() = runBlocking {
        val repo = CitaRepositoryFake(retardoCargaMs = 0)
        val guard = OperacionCitaGuard()
        assertTrue(CancelarCitaUseCase(repo, reglas, guard)("C-1").isSuccess)
        val nueva = NuevaCita(CitasSimuladas.paciente.id, "Medicina General", "nana",
            (Clock.System.now() + 20.days).toLocalDateTime(TimeZone.currentSystemDefault()), "Control preventivo",
            ModalidadAtencion.TELECONSULTA)
        assertTrue(SolicitarCitaUseCase(repo, reglas, guard)(nueva).isSuccess)
        assertEquals(ModalidadAtencion.TELECONSULTA, repo.obtenerCitas().last().modalidad)
        assertEquals(3, repo.obtenerCitas().count { it.estado is EstadoCita.Programada })
    }

    @Test fun solicitudesConcurrentesNoSuperanTresProgramadas() = runBlocking {
        val repo = CitaRepositoryFake(citas.take(2), retardoCargaMs = 0)
        val uso = SolicitarCitaUseCase(repo, reglas, OperacionCitaGuard())
        val nuevas = listOf(20, 21).map { dias ->
            NuevaCita(CitasSimuladas.paciente.id, "Medicina General", "nana",
                (Clock.System.now() + dias.days).toLocalDateTime(TimeZone.currentSystemDefault()), "Consulta preventiva",
                ModalidadAtencion.PRESENCIAL)
        }
        val resultados = coroutineScope { nuevas.map { async { uso(it) } }.awaitAll() }
        assertEquals(1, resultados.count { it.isSuccess })
        assertEquals(3, repo.obtenerCitas().count { it.estado is EstadoCita.Programada })
    }

    @Test fun reprogramacionValidaHorarioYGuardaHistorial() = runBlocking {
        val repo = CitaRepositoryFake(citas, retardoCargaMs = 0)
        val uso = ReprogramarCitaUseCase(repo, reglas, OperacionCitaGuard())
        assertTrue(uso("C-1", citas[1].fechaHora).isFailure)
        assertTrue(uso("C-1", kotlinx.datetime.LocalDateTime(citas[0].fechaHora.date,
            kotlinx.datetime.LocalTime(citas[0].fechaHora.hour, citas[0].fechaHora.minute))).isFailure)
        assertTrue(uso("C-1", (Clock.System.now() - 1.days).toLocalDateTime(TimeZone.currentSystemDefault())).isFailure)
        assertTrue(uso("C-4", (Clock.System.now() + 20.days).toLocalDateTime(TimeZone.currentSystemDefault())).isFailure)
        val nueva = (Clock.System.now() + 20.days).toLocalDateTime(TimeZone.currentSystemDefault())
        val resultado = uso("C-1", nueva).getOrThrow()
        assertEquals(nueva, resultado.fechaHora)
        assertEquals(citas[0].fechaHora, resultado.cambiosProgramacion.single().anterior)
        assertEquals(nueva, repo.obtenerCita("C-1")?.cambiosProgramacion?.single()?.nueva)
        assertEquals(3, reglas.contarProgramadas(repo.obtenerCitas(), CitasSimuladas.paciente.id))
    }
}
