# AndinaSalud

Aplicación móvil de gestión de citas médicas para Android e iOS. El proyecto usa datos simulados exclusivamente en memoria.

## Tecnologías

Kotlin Multiplatform, Compose Multiplatform, Material 3, Kotlin Coroutines, StateFlow, ViewModel multiplataforma, Koin y `kotlinx-datetime`. El target iOS está configurado para dispositivo y simulador ARM64.

## Arquitectura

- `domain/model`: entidades `Paciente`, `Sede`, `Medico`, `Cita` y `EstadoCita` sellado.
- `domain/repository`: contrato `CitaRepository`, independiente de UI y plataforma.
- `domain/usecase`: consulta, solicitud y cancelación; `ReglasCita` centraliza RN-01 a RN-05. `OperacionCitaGuard` serializa las operaciones de escritura durante esta sesión en memoria.
- `data/local`: catálogo y seis citas de ejemplo. Las programadas se generan desde el reloj actual.
- `data/repository`: implementación `CitaRepositoryFake` con lista mutable en memoria protegida por `Mutex`. Puede construirse con una lista vacía o un fallo de lectura para verificar estados sin alterar pantallas.
- `presentation`: ViewModels con `StateFlow` privado mutable, `UiState`, composables y navegación.
- `di`: módulo común de Koin; Android e iOS inicializan el contenedor en sus entradas.

## Estructura de paquetes

`shared/src/commonMain/kotlin/pe/upeu/andinasalud` contiene dominio, datos, presentación y DI. `androidApp` contiene la actividad Android; `iosApp` abre el controlador Compose de `shared`.

## Flujo de datos

`CitasSimuladas → CitaRepositoryFake → CitaRepository → UseCase → ViewModel → StateFlow → UiState → Screen`. Para conectar una API futura, se implementa otro `CitaRepository` y se reemplaza el registro de Koin; dominio y pantallas no dependen del repositorio concreto.

## Reglas de negocio

| Regla | Implementación |
| --- | --- |
| RN-01 | Fecha y hora estrictamente futuras en `ReglasCita.validarFecha`. |
| RN-02 | Máximo tres citas programadas por paciente en `ReglasCita.validarCupo`. |
| RN-03 | Solo cancelación de una programada a más de 24 horas en `ReglasCita.puedeCancelar`. |
| RN-04 | Motivo de 10 a 200 caracteres en `ReglasCita.validarMotivo`. |
| RN-05 | Sin duplicar fecha y hora programadas para un paciente en `ReglasCita.validarHorario`. |

`SolicitarCitaUseCase` y `CancelarCitaUseCase` aplican estas reglas antes de escribir en el repositorio. El formulario ubica los errores bajo el campo correspondiente.

Las tres citas iniciales ya ocupan el cupo RN-02. Para demostrar una solicitud exitosa, cancela primero una cita programada a más de 24 horas y después solicita otra.

## Datos simulados

Un paciente fijo, cuatro sedes (Ñaña, Chosica, Chaclacayo y Santa Anita), cinco especialidades, diez médicos y seis citas iniciales: tres programadas futuras, dos atendidas y una cancelada. Los cambios se pierden al cerrar el proceso.

## Ejecución Android

1. Abrir la carpeta raíz en Android Studio y esperar la sincronización Gradle.
2. Instalar Android SDK 37 y seleccionar un emulador o teléfono con API 34 o superior.
3. Ejecutar la configuración `androidApp` o `./gradlew :androidApp:assembleDebug` (en Windows, `.\gradlew.bat :androidApp:assembleDebug`).
4. El APK de depuración queda en `androidApp/build/outputs/apk/debug/`.

## Ejecución iOS

Se necesita macOS con Xcode y un simulador iOS ARM64 o un dispositivo configurado. Abrir `iosApp/iosApp.xcodeproj` en Xcode, seleccionar el equipo de firma si se usa dispositivo, elegir el destino y ejecutar. Verificar allí compilación del framework `Shared`, apertura, navegación, retorno, formulario y tema. Este entorno Windows no puede ejecutar Xcode ni afirmar una prueba iOS exitosa.

## Trabajo individual

La evaluación se desarrolla individualmente según la indicación actual del docente. El historial debe conservar un solo autor real; no se crean revisiones cruzadas ni solicitudes de incorporación ficticias. La Parte II (SC-A, SC-B, SC-C o SC-D) se implementará cuando se asigne, en una rama `sc-<letra>-gutierrez` creada desde `develop`.

## Alcance de pantallas

Los requisitos describen cinco pantallas: Inicio, Citas, Detalle, Solicitud y Perfil/Ajustes. La lista de cotejo menciona seis sin identificar una sexta funcionalidad. Perfil y Ajustes comparten pantalla, como permite RF-06; se deja esta ambigüedad documentada.

## Preparación para la Parte II

- SC-A: `CitasViewModel.filtrar` combina filtros fuera del Composable.
- SC-B: `ReglasCita.validarCupo` es la única regla de límite; la UI futura debe leer su resultado desde un ViewModel.
- SC-C: `Cita` y `NuevaCita` son los puntos del dominio para incorporar modalidad, que después recorrería repositorio y pantallas.
- SC-D: `ReglasCita.validarFecha` y `validarHorario(..., exceptoId)` permiten reutilizar las validaciones al reprogramar.

Ninguna solicitud SC está implementada en la aplicación base.

## Verificación

En Windows se ejecutaron `:shared:testAndroidHostTest` y `:androidApp:assembleDebug`: 10 pruebas, 0 fallos y APK generado. Las pruebas revisan reglas, operaciones concurrentes, cantidades iniciales, búsqueda sin tildes, Koin y estados Cargando/Contenido/Vacío/Error. El emulador conectado no permitió instalar el APK porque su servicio Package Manager devolvió `Broken pipe`; la inspección visual Android sigue pendiente. La compilación/ejecución iOS debe realizarse en Mac.
