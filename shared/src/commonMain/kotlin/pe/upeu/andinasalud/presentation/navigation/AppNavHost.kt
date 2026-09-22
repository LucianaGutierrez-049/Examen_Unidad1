package pe.upeu.andinasalud.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.presentation.citas.*
import pe.upeu.andinasalud.presentation.detalle.*
import pe.upeu.andinasalud.presentation.inicio.*
import pe.upeu.andinasalud.presentation.perfil.*
import pe.upeu.andinasalud.presentation.solicitud.*

@Composable
fun AppNavHost(oscuro: Boolean, cambiarTema: (Boolean) -> Unit) {
    val pila = remember { mutableStateListOf<Destino>(Destino.Inicio) }
    val actual = pila.last()
    fun volver() {
        if (pila.size > 1) pila.removeAt(pila.lastIndex)
        else if (pila.last() != Destino.Inicio) { pila.clear(); pila.add(Destino.Inicio) }
    }
    fun principal(destino: Destino) { pila.clear(); pila.add(destino) }
    fun abrir(destino: Destino) { pila.add(destino) }
    PlatformBackHandler(pila.size > 1 || actual != Destino.Inicio, ::volver)

    val inicioVM: InicioViewModel = koinViewModel()
    val citasVM: CitasViewModel = koinViewModel()
    val detalleVM: DetalleCitaViewModel = koinViewModel()
    val solicitudVM: SolicitudViewModel = koinViewModel()
    val perfilVM: PerfilViewModel = koinViewModel()
    val inicio by inicioVM.uiState.collectAsState()
    val citas by citasVM.uiState.collectAsState()
    val detalle by detalleVM.uiState.collectAsState()
    val solicitud by solicitudVM.uiState.collectAsState()
    val perfil by perfilVM.uiState.collectAsState()

    LaunchedEffect(actual) {
        when (actual) {
            Destino.Inicio -> { inicioVM.cargar(); citasVM.cargar() }
            Destino.Citas -> citasVM.cargar()
            Destino.Perfil -> perfilVM.cargar()
            is Destino.Detalle -> detalleVM.cargar(actual.id)
            Destino.Solicitud -> solicitudVM.reiniciar()
        }
    }
    Scaffold(bottomBar = {
        if (actual is Destino.Inicio || actual is Destino.Citas || actual is Destino.Perfil) {
            NavigationBar {
                listOf(Triple(Destino.Inicio, "Inicio", "⌂"), Triple(Destino.Citas, "Citas", "▤"),
                    Triple(Destino.Perfil, "Perfil", "●")).forEach { (destino, titulo, icono) ->
                    NavigationBarItem(selected = actual == destino, onClick = { principal(destino) },
                        icon = { Text(icono) }, label = {
                            Text(if (destino == Destino.Citas) "Citas ${citas.programadas?.let { "$it/${citas.limiteProgramadas}" } ?: "…"}" else titulo)
                        })
                }
            }
        }
    }) { padding ->
        androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
            when (actual) {
                Destino.Inicio -> InicioScreen(inicio, inicioVM::cargar, { principal(Destino.Citas) },
                    { abrir(Destino.Solicitud) }, { abrir(Destino.Detalle(it)) }, citas.puedeSolicitar)
                Destino.Citas -> CitasScreen(citas, citasVM::buscar, citasVM::seleccionarFiltro, citasVM::seleccionarHoy,
                    citasVM::cargar, { abrir(Destino.Detalle(it)) }, { abrir(Destino.Solicitud) })
                Destino.Perfil -> PerfilScreen(perfil, perfilVM::cargar, oscuro, cambiarTema)
                is Destino.Detalle -> DetalleCitaScreen(detalle, ::volver, { detalleVM.cargar(actual.id) },
                    { detalleVM.cancelar(actual.id) }, detalleVM::alternarReprogramacion,
                    detalleVM::fecha, detalleVM::hora, { detalleVM.confirmarReprogramacion(actual.id) })
                Destino.Solicitud -> SolicitudScreen(solicitud, ::volver, { principal(Destino.Citas) },
                    solicitudVM::cargar, solicitudVM::especialidad, solicitudVM::sede, solicitudVM::fecha,
                    solicitudVM::hora, solicitudVM::motivo, solicitudVM::modalidad, solicitudVM::enviar)
            }
        }
    }
}
