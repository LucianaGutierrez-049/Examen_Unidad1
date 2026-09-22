package pe.upeu.andinasalud.presentation.common

sealed interface LoadState<out T> {
    data object Cargando : LoadState<Nothing>
    data object Vacio : LoadState<Nothing>
    data class Contenido<T>(val datos: T) : LoadState<T>
    data class Error(val mensaje: String) : LoadState<Nothing>
}
