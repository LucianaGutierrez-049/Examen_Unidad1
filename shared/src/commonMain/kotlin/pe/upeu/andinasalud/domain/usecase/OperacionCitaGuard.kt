package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Serializa la lectura, validación y escritura de citas dentro de esta sesión en memoria. */
class OperacionCitaGuard {
    private val mutex = Mutex()

    suspend fun <T> ejecutar(operacion: suspend () -> T): T = mutex.withLock { operacion() }
}
