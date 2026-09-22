package pe.upeu.andinasalud

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.koin.dsl.koinApplication
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.di.appModule
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ReglasCita
import pe.upeu.andinasalud.presentation.citas.FiltroEstado
import pe.upeu.andinasalud.data.local.CitasSimuladas
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.common.LoadState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class CitasViewModelTest {
    @Test fun representaCargaContenidoYVacio() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = CitaRepositoryFake(retardoCargaMs = 800)
            val vm = CitasViewModel(ObtenerCitasUseCase(repo), repo, ReglasCita())
            vm.cargar()
            assertIs<LoadState.Cargando>(vm.uiState.value.carga)
            advanceTimeBy(800)
            runCurrent()
            val contenido = assertIs<LoadState.Contenido<*>>(vm.uiState.value.carga)
            assertEquals(6, (contenido.datos as List<*>).size)
            assertEquals(3, vm.uiState.value.programadas)
            assertEquals(false, vm.uiState.value.puedeSolicitar)
            vm.buscar("nutricion")
            assertEquals(1, (assertIs<LoadState.Contenido<*>>(vm.uiState.value.carga).datos as List<*>).size)
            vm.buscar("especialidad inexistente")
            assertIs<LoadState.Vacio>(vm.uiState.value.carga)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun representaErrorSinCambiarLaPantalla() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = CitaRepositoryFake(falloLectura = true, retardoCargaMs = 800)
            val vm = CitasViewModel(ObtenerCitasUseCase(repo), repo, ReglasCita())
            vm.cargar()
            advanceTimeBy(800)
            runCurrent()
            assertIs<LoadState.Error>(vm.uiState.value.carga)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun hoySeCombinaConFiltroDeEstado() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val base = CitasSimuladas.citasIniciales()
            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val repo = CitaRepositoryFake(listOf(base[0].copy(fechaHora = hoy),
                base[3].copy(fechaHora = hoy), base[1]), retardoCargaMs = 0)
            val vm = CitasViewModel(ObtenerCitasUseCase(repo), repo, ReglasCita())
            vm.cargar()
            runCurrent()
            assertEquals(true, vm.uiState.value.puedeSolicitar)
            vm.seleccionarHoy()
            assertEquals(2, (assertIs<LoadState.Contenido<*>>(vm.uiState.value.carga).datos as List<*>).size)
            vm.seleccionarFiltro(FiltroEstado.PROGRAMADAS)
            assertEquals(1, (assertIs<LoadState.Contenido<*>>(vm.uiState.value.carga).datos as List<*>).size)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun koinResuelveContratoYCasosDeUso() {
        val aplicacion = koinApplication { modules(appModule) }
        assertNotNull(aplicacion.koin.get<CitaRepository>())
        assertNotNull(aplicacion.koin.get<SolicitarCitaUseCase>())
        aplicacion.close()
    }
}
