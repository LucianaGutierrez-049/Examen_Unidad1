package pe.upeu.andinasalud.presentation.navigation

sealed interface Destino {
    data object Inicio : Destino
    data object Citas : Destino
    data object Perfil : Destino
    data class Detalle(val id: String) : Destino
    data object Solicitud : Destino
}
