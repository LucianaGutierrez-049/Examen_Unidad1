package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake(
    citasIniciales: List<Cita> = CitasSimuladas.citasIniciales(),
    private val falloLectura: Boolean = false,
    private val retardoCargaMs: Long = 800,
) : CitaRepository {
    private val mutex = Mutex()
    private val citas = citasIniciales.toMutableList()
    private fun comprobarLectura() { if (falloLectura) error("Error simulado de carga") }
    override suspend fun obtenerPaciente(): Paciente { comprobarLectura(); return CitasSimuladas.paciente }
    override suspend fun obtenerSedes(): List<Sede> { comprobarLectura(); return CitasSimuladas.sedes }
    override suspend fun obtenerEspecialidades(): List<String> { comprobarLectura(); return CitasSimuladas.especialidades }
    override suspend fun obtenerMedicos(): List<Medico> { comprobarLectura(); return CitasSimuladas.medicos }
    override suspend fun obtenerCitas(): List<Cita> { delay(retardoCargaMs); comprobarLectura(); return mutex.withLock { citas.toList() } }
    override suspend fun obtenerCita(id: String): Cita? { delay(retardoCargaMs); comprobarLectura(); return mutex.withLock { citas.firstOrNull { it.id == id } } }
    override suspend fun solicitarCita(cita: Cita): Cita = mutex.withLock { citas.add(cita); cita }
    override suspend fun cancelarCita(id: String, motivo: String): Cita = mutex.withLock {
        val index = citas.indexOfFirst { it.id == id }
        require(index >= 0) { "La cita no existe" }
        citas[index] = citas[index].copy(estado = EstadoCita.Cancelada(motivo, true))
        citas[index]
    }
}
