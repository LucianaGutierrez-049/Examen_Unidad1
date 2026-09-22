package pe.upeu.andinasalud.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.inicio.InicioViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

val appModule = module {
    single<CitaRepository> { CitaRepositoryFake() }
    single { ReglasCita() }
    single { OperacionCitaGuard() }
    factory { ObtenerCitasUseCase(get()) }
    single { SolicitarCitaUseCase(get(), get(), get()) }
    single { CancelarCitaUseCase(get(), get(), get()) }
    viewModel { InicioViewModel(get(), get()) }
    viewModel { CitasViewModel(get()) }
    viewModel { DetalleCitaViewModel(get(), get(), get()) }
    viewModel { SolicitudViewModel(get(), get()) }
    viewModel { PerfilViewModel(get()) }
}

fun iniciarKoin() = startKoin { modules(appModule) }
