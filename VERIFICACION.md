# Verificación del examen

`CUMPLE` indica implementación en código y, cuando aplica, prueba automatizada Android. La inspección visual Android y ejecución iOS siguen pendientes de dispositivo/Mac.

## Requisitos funcionales

| Código | Archivo y función principal | Estado | Evidencia |
| --- | --- | --- | --- |
| RF-01 Inicio | `presentation/inicio/InicioScreen.kt` · `InicioScreen`; `InicioViewModel.kt` | CUMPLE | Saludo, próxima cita y accesos. |
| RF-02 Lista | `presentation/citas/CitasScreen.kt` · `CitasScreen`; `CitasViewModel.kt` | CUMPLE | `LazyColumn`, orden desde el caso de uso y filtros por estado. |
| RF-03 Detalle | `presentation/detalle/DetalleCitaScreen.kt` · `DetalleCitaScreen`; `DetalleCitaViewModel.kt` | CUMPLE | Datos, indicaciones, diálogo y cancelación validada. |
| RF-04 Solicitud | `presentation/solicitud/SolicitudScreen.kt` · `SolicitudScreen`; `SolicitudViewModel.kt` | CUMPLE | Cinco campos y errores debajo de cada uno. |
| RF-05 Búsqueda | `presentation/citas/CitasViewModel.kt` · `normalizar`, `filtrar` | CUMPLE | Especialidad/médico, sin distinguir caso ni tildes; prueba de normalización. |
| RF-06 Perfil/Tema | `presentation/perfil/PerfilScreen.kt` · `PerfilScreen`; `App.kt` | CUMPLE | Datos del paciente y tema elevado a la raíz. |
| RF-07 Navegación | `presentation/navigation/AppNavHost.kt` · `AppNavHost` | CUMPLE | Tres destinos inferiores, detalle, solicitud y atrás Android. |
| RF-08 Estados UI | `presentation/common/LoadState.kt` · `LoadState`; ViewModels; `CitaRepositoryFake.kt` | CUMPLE | Cargando, contenido, vacío y error; `delay(800)` sin bloqueo. |

## Reglas de negocio

| Código | Archivo y función principal | Estado | Evidencia |
| --- | --- | --- | --- |
| RN-01 Fecha futura | `domain/usecase/ReglasCita.kt` · `validarFecha` | CUMPLE | `ReglasCitaTest.fechaPasadaYMotivoFueraDeRango`. |
| RN-02 Máximo 3 | `domain/usecase/ReglasCita.kt` · `validarCupo` | CUMPLE | `ReglasCitaTest.cupoYHorario`. |
| RN-03 Cancelación >24h | `domain/usecase/ReglasCita.kt` · `puedeCancelar` | CUMPLE | `ReglasCitaTest.cancelacionSoloProgramadaConMasDeUnDia`. |
| RN-04 Motivo 10–200 | `domain/usecase/ReglasCita.kt` · `validarMotivo` | CUMPLE | Casos de 5, 201 y longitud válida. |
| RN-05 No duplicar horario | `domain/usecase/ReglasCita.kt` · `validarHorario` | CUMPLE | Coincidencia por paciente, fecha y hora. |

## Requisitos técnicos

| Requisito | Archivo y función/clase principal | Estado | Evidencia |
| --- | --- | --- | --- |
| KMP | `shared/build.gradle.kts` · `kotlin` | CUMPLE | Targets Android e iOS declarados. |
| Android | `androidApp/build.gradle.kts` · `android` | CUMPLE | `assembleDebug` completado. |
| iOS target | `shared/build.gradle.kts` · `iosArm64`, `iosSimulatorArm64` | CUMPLE | Configurado; ejecución pendiente en Mac. |
| Compose Multiplatform | `shared/build.gradle.kts` · plugin Compose | CUMPLE | UI común compila para Android. |
| Material 3 | `presentation/theme/AndinaSaludTheme.kt` · `MaterialTheme` | CUMPLE | Esquemas propios. |
| Tema claro/oscuro | `presentation/theme/AndinaSaludTheme.kt` · `AndinaSaludTheme`; `App.kt` | CUMPLE | Estado único de app. |
| Clean Architecture | `domain/`, `data/`, `presentation/` | CUMPLE | Contrato de repositorio en dominio. |
| MVVM | `presentation/*/*ViewModel.kt` | CUMPLE | ViewModels separados de composables. |
| StateFlow | `presentation/*/*ViewModel.kt` · `uiState` | CUMPLE | Mutable privado, lectura pública. |
| UiState | `presentation/common/LoadState.kt` · `LoadState`; `*UiState.kt` | CUMPLE | Diferente de modelos de dominio. |
| Koin | `di/AppModule.kt` · `appModule`, `iniciarKoin` | CUMPLE | Repositorio, casos de uso y ViewModels registrados. |
| Coroutines | `CitaRepositoryFake.kt` · `obtenerCitas`; ViewModels | CUMPLE | `delay(800)` y `viewModelScope`. |
| Interfaz repositorio | `domain/repository/CitaRepository.kt` · `CitaRepository` | CUMPLE | Sin dependencias UI. |
| Repositorio fake | `data/repository/CitaRepositoryFake.kt` · `CitaRepositoryFake` | CUMPLE | Lista en memoria. |
| UseCases | `domain/usecase/` · `ObtenerCitasUseCase`, `SolicitarCitaUseCase`, `CancelarCitaUseCase` | CUMPLE | Reglas aplicadas antes de escribir. |
| LazyColumn | `presentation/citas/CitasScreen.kt` · `CitasScreen` | CUMPLE | Listado perezoso. |
| State hoisting | `presentation/navigation/AppNavHost.kt` · `AppNavHost`; pantallas | CUMPLE | Datos y callbacks llegan a composables. |
| Datos en memoria | `data/local/CitasSimuladas.kt` · `CitasSimuladas` | CUMPLE | Seis citas, cuatro sedes y diez médicos. |
| Sin librerías prohibidas | `gradle/libs.versions.toml`, `shared/build.gradle.kts` | CUMPLE | Sin Ktor, Retrofit, Room, SQLDelight, Firebase, red ni BD. |

## Validación manual pendiente

- Android: abrir el APK en emulador/teléfono y recorrer las cinco pantallas, atrás, tema, creación y cancelación.
- iOS: compilar/ejecutar en Mac con Xcode y repetir el mismo recorrido.
- Capturas: Inicio, Citas sin filtro y con filtro/búsqueda, Detalle, Solicitud con validación y Perfil en claro/oscuro; repetir en Android e iOS. Adjuntar también gráfico Git y `git shortlog -sne` reales.
