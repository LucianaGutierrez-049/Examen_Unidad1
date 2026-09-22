package pe.upeu.andinasalud

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ReglasCita
import pe.upeu.andinasalud.presentation.citas.normalizar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

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
        assertNotNull(reglas.validarHorario(citas, CitasSimuladas.paciente.id, citas[0].fechaHora))
        assertNull(reglas.validarHorario(citas, "otro-paciente", citas[0].fechaHora))
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
}
