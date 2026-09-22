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

La evaluación se desarrolla individualmente según la indicación actual del docente. El historial debe conservar un solo autor real; no se crean revisiones cruzadas ni solicitudes de incorporación ficticias. Los cuatro cambios SC-A–SC-D están implementados como preparación; la solicitud concreta que asigne el docente y su evidencia de implementación en vivo se deben documentar por separado.

## Alcance de pantallas

Los requisitos describen cinco pantallas: Inicio, Citas, Detalle, Solicitud y Perfil/Ajustes. La lista de cotejo menciona seis sin identificar una sexta funcionalidad. Perfil y Ajustes comparten pantalla, como permite RF-06; se deja esta ambigüedad documentada.

## Cambios de la Parte II

- SC-A: el chip «Hoy» se combina con el filtro de estado en `CitasViewModel`, sin lógica de fecha en el Composable.
- SC-B: la barra inferior muestra el número de citas programadas y los accesos a solicitud se deshabilitan al alcanzar el límite definido en `ReglasCita`.
- SC-C: la modalidad Presencial/Teleconsulta está en el modelo de dominio, los datos simulados, el formulario, la lista y el detalle, con iconos distintos.
- SC-D: se reprograma una cita Programada desde el detalle; `ReprogramarCitaUseCase` reutiliza las reglas de fecha y horario, y el detalle muestra el historial de cambios.

Las cuatro implementaciones están cubiertas por compilación y pruebas Android; falta el recorrido manual de las nuevas interfaces y la verificación iOS en macOS.

## Verificación

En Windows se ejecutaron `:shared:testAndroidHostTest` y `:androidApp:assembleDebug`: 12 pruebas, 0 fallos y APK generado. Las pruebas revisan reglas, operaciones concurrentes, cantidades iniciales, búsqueda sin tildes, Koin, estados Cargando/Contenido/Vacío/Error y los cambios SC-A–SC-D. La ejecución manual del APK recién generado y la compilación/ejecución iOS siguen pendientes.
