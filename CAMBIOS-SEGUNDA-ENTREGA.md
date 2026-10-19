# Cambios de la segunda entrega (puntos 7.1, 7.2 y 7.3)

Copia de trabajo del proyecto original. Los puntos 7.4 (patrones) y 7.5 (mejoras generales) quedan para discutir en el grupo.

Documentación completa (con la propuesta de cambios al Documento de Requerimientos y los diagramas):
`docs/Cambios Segunda Entrega - Grupo 10 v3.docx` (incluye el detalle de cada clase nueva y de cada clase modificada). Modelo UML: `uml/FestivalCine.mdj`, un único diagrama con las 118 clases, **todos** sus atributos y métodos y las 140 relaciones (verificación contra el código en `uml/Verificacion del UML.md`; imágenes en `uml/imagenes/`).

## 7.1 Reglas de negocio

| Problema | Solución | Dónde |
|---|---|---|
| Se vendían entradas sueltas de funciones que ya pasaron | `Funcion.emitirEntrada` rechaza la venta si la función ya comenzó. Toda venta y toda reserva pasa por ese método, así que la regla vale para las dos. La pantalla de venta solo ofrece funciones que no comenzaron. | `modelo/funciones/Funcion.java`, `FuncionServicio.getFuncionesPresencialesEnVenta()` |
| Clases del modelo todas juntas | El paquete `modelo` se dividió en `comun`, `precios`, `personas`, `peliculas`, `festival`, `funciones` y `abonos`. | `src/modelo/*` |
| `yaPaso()` comparaba solo la fecha | Ahora compara fecha **y hora** con `LocalDateTime.now(reloj)`. | `Funcion.yaPaso()` |
| Un jurado podía evaluar la misma película varias veces | `Pelicula.recibirEvaluacion` verifica `fueEvaluadaPor(jurado)` y lanza `EntidadDuplicadaException`. | `Pelicula.java`, `Calificaciones.java` |
| La superposición ignoraba la duración | Cada función ocupa el intervalo [inicio, inicio + duración). `seSuperponeEnHorarioCon` compara intervalos. Se aplica a funciones de una misma sala y a reservas de un mismo abono. | `Funcion.java`, `FuncionPresencial.ocupaLaSala`, `Abono.validarReserva`, `Pelicula.terminaSiEmpiezaA` |
| Las funciones no se validaban contra la edición | Solo se programan funciones de películas que compiten en una edición, y dentro de sus fechas. Además, una película con funciones no puede dejar de competir en su edición. | `FuncionServicio.validarProgramacion`, `Edicion.validarFechaDeFuncion`, `Edicion.quitarDeSeccion` |
| `recibirVoto` creaba el voto antes de validar | Primero valida y después crea el voto. | `Pelicula.recibirVoto` |

En `evaluaciones.txt` había un caso real del bug: Pedro Martinez evaluaba *Roma* dos veces (9.0 y 10.0). Se dejó la primera evaluación.

## 7.2 Persistencia

| Problema | Solución | Dónde |
|---|---|---|
| Solo se guardaban 7 de 14 tipos de datos | Se guardan los 14 tipos. Archivos nuevos: `reparto`, `ediciones`, `secciones`, `secciones_peliculas`, `funciones`, `abonos`, `entradas`, `votos`. Las referencias usan claves: email para personas, título para películas, código para funciones, entradas y abonos. Al recargar se restauran butacas ocupadas, reservas y precios, y la numeración de códigos sigue después del último. | `persistencia/ArchivoDatos`, `CargaDeArchivos`, `InstantaneaDeArchivos` |
| CSV sin escape | Formato CSV con comillas (RFC 4180): un campo con coma o comillas va entre comillas. | `persistencia/FormatoCsv.java` |
| Una línea inválida cortaba toda la carga | Cada línea se carga por separado. Si falla, se anota el error y se sigue con la siguiente. Al final, Main muestra todos los errores juntos. | `CargaDeArchivos.leer`, `PersistenciaException.getErrores()`, `Main` |
| Guardado en otro hilo sin sincronización | La copia de los datos (`prepararGuardado`) se toma en el hilo de la UI y el otro hilo solo escribe esa copia. Los guardados se encolan en un único hilo. Cada archivo se escribe en un temporal y después se reemplaza. Al salir, la ventana espera a que termine el guardado y, si falla, lo informa y pregunta si salir igual. | `Persistencia.Guardado`, `VentanaPrincipal.guardar` / `confirmarSalida`, `ArchivoTexto.escribirRegistros` |

> Como ahora se guarda todo, la demo (`InicializadorDatos`) solo se genera cuando no hay ediciones guardadas, es decir la primera vez.

## 7.3 Code smells

| Smell | Solución | Dónde |
|---|---|---|
| Large Class (`Pelicula`) | Se extrajo `Calificaciones` (evaluaciones, votos y promedios). `Pelicula` valida las reglas y delega en ella. | `modelo/peliculas/Calificaciones.java` |
| Long Method en la UI | Cada diálogo en su clase: `DialogoAgregarSeccion`, `DialogoComprarAbono`, `DialogoReservaAbono`, `DialogoVentaEntrada` y `DialogoFuncion` (Template Method) con `DialogoFuncionPresencial` y `DialogoFuncionStreaming`. | `ui/festival`, `ui/entradas`, `ui/funciones` |
| Primitive Obsession | Precios: `Dinero` (BigDecimal, 2 decimales). Descuentos: `Porcentaje`. Butaca: record `Butaca` y `Optional<Butaca>` (antes `0` = "sin butaca"). Funciones con código propio (`F-n`). | `modelo/precios`, `modelo/funciones/Butaca.java` |
| Identidad por nombre | Las personas se identifican por **email** y puede haber homónimos. `equals`/`hashCode` en Persona, Pelicula, Sala, Edicion, Funcion, Entrada y Abono. Los combos de la UI guardan objetos y muestran "nombre &lt;email&gt;". | `Persona.java`, `PersonaServicio`, `SelectorPersona`, `ComponentesUI.crearCombo` |
| Null como valor | `Entrada.getAbono()` devuelve `Optional<Abono>`. | `Entrada.java`, `SolicitudEntrada.java` |
| Ley de Demeter | `VentanaPrincipal` ya no expone `getSistema()`. Cada panel recibe en su constructor solo los servicios que usa. | `VentanaPrincipal.construirBarraMenu`, todos los paneles |
| Código duplicado | El cálculo de porcentaje está solo en `Porcentaje`. El criterio "mejor promedio" está solo en `Premiacion.mejorPromedio`, que usan Seccion y Edicion. | `modelo/precios/Porcentaje.java`, `modelo/festival/Premiacion.java` |
| Excepciones genéricas | `FestivalException` es abstracta y se usan excepciones específicas: `DatoInvalidoException`, `EntidadDuplicadaException`, `ReglaNegocioException` (y sus subclases). La UI atrapa `FestivalException`, no `RuntimeException`/`Exception`. | `excepciones/*`, `FormularioDialogo` |
| Dependencia oculta del reloj | Se inyecta `java.time.Clock` desde `Main` en funciones, `AbonoTresDias`, `DescuentoCompraAnticipada`, servicios, persistencia y demo. | `Main`, `SistemaFestival`, `Funcion`, `AbonoTresDias`, `Tarifario` |
| Inconsistencia de precios | Los abonos también pasan por el `Tarifario` (`calcularPrecioAbono`, con `ReglaDescuentoAbono`). | `servicios/precios/*`, `AbonoServicio.cotizar` |
| Visibilidad inconsistente | El abono se registra solo en su espectador al emitirse (`AbonoX.emitir` → `Abono.vincular`), igual que `Entrada.vincular`. `agregarEntrada` y `agregarAbono` tienen la misma visibilidad y solo aceptan lo que pertenece a ese espectador. | `modelo/abonos/*`, `Espectador.java` |

## Clases nuevas (25)

| Capa | Clases |
|---|---|
| Modelo | `Calificaciones`, `Premiacion`, `Dinero`, `Porcentaje`, `Butaca` |
| Excepciones | `DatoInvalidoException`, `EntidadDuplicadaException`, `ReglaNegocioException` |
| Servicios | `ReglaDescuentoAbono` |
| Persistencia | `Persistencia.Guardado`, `ArchivoDatos`, `CargaDeArchivos` (con `CargaDeArchivos.Conversion`), `InstantaneaDeArchivos`, `Registro`, `FormatoCsv` |
| Inicialización | `PersonasDemo` |
| Interfaz | `SelectorPersona`, `DialogoAgregarSeccion`, `DialogoComprarAbono`, `DialogoReservaAbono`, `DialogoVentaEntrada`, `DialogoFuncion`, `DialogoFuncionPresencial`, `DialogoFuncionStreaming` |

De las 90 clases que ya existían, 60 cambiaron sus atributos o métodos, 21 cambiaron solo por dentro y 9 quedaron iguales (el detalle está en el Word, sección 7).

## Cosas a tener en cuenta (para charlar en el grupo)

- **Paquetes y visibilidad:** al separar el modelo en subpaquetes, algunos métodos que antes eran package-private tuvieron que pasar a públicos, porque las clases que los llaman quedaron en otro paquete. Son `Artista.agregarPelicula`, `Jurado.registrarEvaluacion`, `Espectador.agregarEntrada/agregarAbono` y `Abono.agregarReserva/cubrePresencial/cubreStreaming`. Para que no se usen mal, cada uno verifica que el otro lado ya tenga registrado el vínculo. Por eso `Funcion` y `Entrada` quedaron juntas en `modelo/funciones`: así sus métodos internos (`vincular`, `liberarButaca`) siguen siendo package-private.
- **Formato de archivos:** `peliculas.txt` y `evaluaciones.txt` ahora referencian al director y al jurado **por email** (antes por nombre). Los archivos de esta carpeta ya están convertidos.
- **Edición actual de la demo:** antes era fija (julio de 2026). Ahora va desde la fecha del día y dura 30 días, porque si no, las funciones de la demo quedaban fuera de su edición. Algunas películas cambiaron de edición para que las que tienen funciones compitan en la actual.
- **`Pelicula` sigue teniendo bastantes líneas:** son métodos de una línea que delegan en `Calificaciones` (es lo que propone el punto). Si se prefiere, la UI podría pedirle directamente las calificaciones a la película.
- **Clases grandes:** con el UML completo se ve que crecieron `Funcion` (de 23 a 32 métodos), `Pelicula` (de 25 a 30), `Edicion` (de 16 a 22), `Abono` (de 19 a 22) y la nueva `CargaDeArchivos` (23). Conviene revisarlo con los puntos 7.4 y 7.5.
