# Verificación del examen

`CUMPLE` indica evidencia en código, compilación o prueba automatizada según la fila. `PARCIAL` indica que falta observar el requisito funcional en un dispositivo. `NO VERIFICABLE` identifica una comprobación que este entorno no pudo completar. No se equipara compilar con ejecutar visualmente.

## Requisitos funcionales

| Código | Archivo y función principal | Estado | Evidencia |
| --- | --- | --- | --- |
| RF-01 Inicio | `presentation/inicio/InicioScreen.kt` · `InicioScreen`; `InicioViewModel.kt` | PARCIAL | Saludo, próxima cita y accesos implementados; falta verlo en dispositivo. |
| RF-02 Lista | `presentation/citas/CitasScreen.kt` · `CitasScreen`; `CitasViewModel.kt`; `ObtenerCitasUseCase.kt` | PARCIAL | `LazyColumn`, futuras primero, historial después y filtros; orden probado, visual pendiente. |
| RF-03 Detalle | `presentation/detalle/DetalleCitaScreen.kt` · `DetalleCitaScreen`; `DetalleCitaViewModel.kt` | PARCIAL | Datos, indicaciones, diálogo y cancelación validada; interacción visual pendiente. |
| RF-04 Solicitud | `presentation/solicitud/SolicitudScreen.kt` · `SolicitudScreen`; `SolicitudViewModel.kt` | PARCIAL | Cinco campos, errores por campo y fallo de operación manejado; interacción visual pendiente. |
| RF-05 Búsqueda | `presentation/citas/CitasViewModel.kt` · `normalizar`, `filtrar` | CUMPLE | Especialidad/médico, sin distinguir caso ni tildes; prueba de normalización. |
| RF-06 Perfil/Tema | `presentation/perfil/PerfilScreen.kt` · `PerfilScreen`; `App.kt` | PARCIAL | Datos y tema global en código; cambio visual pendiente. |
| RF-07 Navegación | `presentation/navigation/AppNavHost.kt` · `AppNavHost` | PARCIAL | Tres destinos, detalle, solicitud y atrás implementados; recorrido en dispositivo pendiente. |
| RF-08 Estados UI | `presentation/common/LoadState.kt` · `LoadState`; ViewModels; `CitaRepositoryFake.kt` | CUMPLE | Cargando, contenido, vacío y error probados en `CitasViewModelTest`; `delay(800)` con reloj de prueba. |

## Reglas de negocio

| Código | Archivo y función principal | Estado | Evidencia |
| --- | --- | --- | --- |
| RN-01 Fecha futura | `domain/usecase/ReglasCita.kt` · `validarFecha` | CUMPLE | `ReglasCitaTest.fechaPasadaYMotivoFueraDeRango`. |
| RN-02 Máximo 3 | `domain/usecase/ReglasCita.kt` · `validarCupo`; `OperacionCitaGuard.kt` | CUMPLE | Prueba de dos solicitudes concurrentes: una admitida y máximo tres programadas. |
| RN-03 Cancelación >24h | `domain/usecase/ReglasCita.kt` · `puedeCancelar` | CUMPLE | `ReglasCitaTest.cancelacionSoloProgramadaConMasDeUnDia`. |
| RN-04 Motivo 10–200 | `domain/usecase/ReglasCita.kt` · `validarMotivo` | CUMPLE | Casos de 5, 201 y longitud válida. |
| RN-05 No duplicar horario | `domain/usecase/ReglasCita.kt` · `validarHorario` | CUMPLE | Coincidencia por paciente, fecha y hora. |

## Requisitos técnicos

| Requisito | Archivo y función/clase principal | Estado | Evidencia |
| --- | --- | --- | --- |
| KMP | `shared/build.gradle.kts` · `kotlin` | CUMPLE | Targets Android e iOS declarados. |
| Android | `androidApp/build.gradle.kts` · `android` | CUMPLE | `assembleDebug` completado. |
| iOS target | `shared/build.gradle.kts` · `iosArm64`, `iosSimulatorArm64` | CUMPLE | Targets configurados. |
| iOS compilación/ejecución | `shared/src/iosMain`, `iosApp/` | NO VERIFICABLE | Windows no puede ejecutar Xcode ni tareas nativas iOS. |
| Compose Multiplatform | `shared/build.gradle.kts` · plugin Compose | CUMPLE | UI común compila para Android. |
| Material 3 | `presentation/theme/AndinaSaludTheme.kt` · `MaterialTheme` | CUMPLE | Esquemas propios. |
| Tema claro/oscuro | `presentation/theme/AndinaSaludTheme.kt` · `AndinaSaludTheme`; `App.kt` | CUMPLE | Estado único de app. |
| Clean Architecture | `domain/`, `data/`, `presentation/` | CUMPLE | Contrato de repositorio en dominio. |
| MVVM | `presentation/*/*ViewModel.kt` | CUMPLE | ViewModels separados de composables. |
| StateFlow | `presentation/*/*ViewModel.kt` · `uiState` | CUMPLE | Mutable privado, lectura pública. |
| UiState | `presentation/common/LoadState.kt` · `LoadState`; `*UiState.kt` | CUMPLE | Diferente de modelos de dominio. |
| Koin | `di/AppModule.kt` · `appModule`, `iniciarKoin` | CUMPLE | Contrato y caso de uso resueltos en `CitasViewModelTest`. |
| Coroutines | `CitaRepositoryFake.kt` · `obtenerCitas`; ViewModels | CUMPLE | `delay(800)`, `viewModelScope` y prueba de transición a los 800 ms. |
| Interfaz repositorio | `domain/repository/CitaRepository.kt` · `CitaRepository` | CUMPLE | Sin dependencias UI. |
| Repositorio fake | `data/repository/CitaRepositoryFake.kt` · `CitaRepositoryFake` | CUMPLE | Lista en memoria. |
| UseCases | `domain/usecase/` · `ObtenerCitasUseCase`, `SolicitarCitaUseCase`, `CancelarCitaUseCase` | CUMPLE | Reglas aplicadas antes de escribir. |
| LazyColumn | `presentation/citas/CitasScreen.kt` · `CitasScreen` | CUMPLE | Listado perezoso. |
| State hoisting | `presentation/navigation/AppNavHost.kt` · `AppNavHost`; pantallas | CUMPLE | Datos y callbacks llegan a composables. |
| Datos en memoria | `data/local/CitasSimuladas.kt` · `CitasSimuladas` | CUMPLE | Seis citas, cuatro sedes y diez médicos. |
| Sin librerías prohibidas | `gradle/libs.versions.toml`, `shared/build.gradle.kts` | CUMPLE | Sin Ktor, Retrofit, Room, SQLDelight, Firebase, red ni BD. |

## Validación manual pendiente

- Android: abrir el APK en emulador/teléfono y recorrer las cinco pantallas, atrás, tema, creación y cancelación. El emulador detectado devolvió `Broken pipe` desde Package Manager al instalar y listar paquetes, incluso tras reiniciarlo.
- iOS: compilar/ejecutar en Mac con Xcode y repetir el mismo recorrido.
- Capturas: Inicio, Citas sin filtro y con filtro/búsqueda, Detalle, Solicitud con validación y Perfil en claro/oscuro; repetir en Android e iOS. Adjuntar también gráfico Git y `git shortlog -sne` reales.

## Preparación de cambios de Parte II

| Cambio | Estado | Punto de extensión |
| --- | --- | --- |
| SC-A | CUMPLE como preparación | `CitasViewModel.filtrar` combina criterios fuera de la UI. |
| SC-B | CUMPLE como preparación | `ReglasCita.validarCupo` es reutilizable desde un futuro estado del ViewModel. |
| SC-C | CUMPLE como preparación | `Cita` y `NuevaCita` recorren dominio, repositorio y presentación. |
| SC-D | CUMPLE como preparación | `validarFecha` y `validarHorario(..., exceptoId)` reutilizan reglas para reprogramar. |

SC-A, SC-B, SC-C y SC-D siguen sin implementarse en la aplicación base, como se solicitó.
