========================================================
SISTEMA DE GESTIÓN DE FESTIVAL DE CINE
Proceso de Desarrollo de Software - UADE

Grupo 10:
  Juan Martín Ojeda    - Legajo 1175491
  Luis Ferreiro        - Legajo 1208937
  Valentino Pippo      - Legajo 1192965
  Fabricio Boccardo    - Legajo 1112843
  Carina Milano        - Legajo 1227609
========================================================

DESCRIPCIÓN DEL SISTEMA
------------------------
Sistema de gestión para un Festival Internacional de Cine desarrollado en Java.
Permite administrar ediciones del festival y sus secciones, películas,
funciones presenciales y streaming, venta de entradas y abonos, evaluaciones
del jurado, voto del público y determinación de películas ganadoras.
La interfaz es gráfica, desarrollada con Java Swing.


ORGANIZACIÓN DEL PROYECTO
--------------------------
El proyecto está organizado en los siguientes paquetes:

  modelo/         → Entidades del dominio, con sus reglas de negocio,
                    agrupadas por tema:
    comun/        → Validar y Numerador
    precios/      → Dinero, Porcentaje y TipoTarifa
    personas/     → Persona, Artista, Director, Actor, Jurado, Espectador
    peliculas/    → Pelicula, Calificaciones, Evaluacion, VotoPublico, Genero
    festival/     → Festival, Edicion, Seccion, Premio, TipoSeccion, Sala
    funciones/    → Funcion (Presencial / Streaming) y las entradas que emite
                    (Entrada, EntradaPresencial, EntradaStreaming,
                    SolicitudEntrada, Butaca)
    abonos/       → Abono, AbonoTresDias, AbonoFull, AbonoStreaming, TipoAbono
  excepciones/    → Excepciones personalizadas del sistema
  repositorio/    → Interfaz Repositorio<T>, su implementación en memoria
                    y el Catalogo que reúne todos los datos
  servicios/      → Coordinan los casos de uso (un servicio por tema)
    precios/      → Tarifario y reglas de descuento (patrón Strategy)
  persistencia/   → Interfaz Persistencia y su implementación en archivos
  inicializacion/ → Precarga de datos de demostración
  ui/             → Interfaz gráfica (Swing)
    comun/        → Componentes reutilizables: PanelListado, FormularioDialogo,
                    DialogoPersona, SelectorPersona, LectorCampos y ComponentesUI
    peliculas/    → Paneles de gestión de películas, directores y actores
    funciones/    → Panel de funciones y diálogos de alta (DialogoFuncion,
                    DialogoFuncionPresencial, DialogoFuncionStreaming)
    entradas/     → Paneles de venta de entradas, abonos y espectadores, y sus
                    diálogos (DialogoVentaEntrada, DialogoComprarAbono,
                    DialogoReservaAbono)
    evaluaciones/ → Paneles de evaluaciones, ganadores y voto del público
    festival/     → Paneles de ediciones, secciones (DialogoAgregarSeccion) y salas
  app/            → Clase Main (punto de entrada: arma y conecta las piezas)


CLASES DEL MODELO
-----------------
  - Festival, Edicion, Seccion, Premio
  - TipoSeccion (enum): COMPETENCIA_OFICIAL, CINE_INDEPENDIENTE,
    CORTOMETRAJE, NUEVOS_TALENTOS
  - Persona (abstract) → Artista (abstract) → Director, Actor
                       → Jurado, Espectador
  - Pelicula, Genero (enum), Evaluacion
  - Calificaciones: evaluaciones, votos y promedios de una película
  - Funcion (abstract) → FuncionPresencial, FuncionStreaming
  - Entrada (abstract) → EntradaPresencial, EntradaStreaming
  - Sala
  - Abono (abstract) → AbonoTresDias, AbonoFull, AbonoStreaming
  - TipoAbono (enum): TRES_DIAS, FULL, STREAMING (con su precio base)
  - TipoTarifa (enum): GENERAL, ESTUDIANTE, JUBILADO
  - VotoPublico
  - SolicitudEntrada (record): datos con los que se emite una entrada
  - Butaca (record): número de butaca (las entradas streaming no tienen)
  - Dinero y Porcentaje: objetos de valor para precios y descuentos
  - Numerador: números correlativos para los códigos
  - Validar: validaciones de datos comunes


SERVICIOS
---------
Las reglas de negocio viven en las clases del modelo (Experto en
información): por ejemplo, Edicion valida su año y sus fechas,
FuncionPresencial valida y ocupa la butaca, Abono decide si cubre una
función y Pelicula controla que el voto sea de alguien con entrada.
Los servicios coordinan cada caso de uso y dependen de la interfaz
Repositorio, no de listas concretas. La lógica NO se resuelve en la UI
ni en el Main.

  SistemaFestival:
    Crea los servicios y le da a cada uno solo los repositorios que usa.
    Recibe el reloj (java.time.Clock) desde Main. La ventana principal
    recibe este objeto y le pasa a cada panel solo los servicios que usa.

  FestivalServicio:
    Ediciones, secciones (con su premio) y salas. Asocia y quita
    películas de las secciones. Una película compite en una sola edición
    (puede estar en varias secciones de esa edición).

  PersonaServicio:
    Alta y búsqueda de directores, actores, jurados y espectadores.

  PeliculaServicio:
    Películas, reparto y eliminación de películas.

  EvaluacionServicio:
    Evaluaciones del jurado y voto del público.

  FuncionServicio:
    Programación de funciones presenciales y streaming. Una función se
    programa a futuro, para una película que compite en una edición y
    dentro de las fechas de esa edición. En una misma sala no puede haber
    dos funciones superpuestas (se tiene en cuenta la duración).

  VentaServicio:
    Venta de entradas sueltas. El precio lo calcula el Tarifario.

  AbonoServicio:
    Venta de abonos, reservas con abono y cancelación de reservas.

  precios/Tarifario:
    Único lugar que calcula precios, de entradas y de abonos. Aplica una
    lista de ReglaDescuento (DescuentoPorTarifa y DescuentoCompraAnticipada)
    y de ReglaDescuentoAbono (DescuentoPorTarifa). Para agregar un
    descuento nuevo alcanza con escribir otra regla.

  inicializacion/InicializadorDatos:
    Ejecuta en orden un Inicializador por tipo de dato (ediciones,
    secciones, reparto, funciones, entradas, abonos y votos). Solo corre
    la primera vez (cuando no hay ediciones guardadas).


INTERFAZ GRÁFICA
----------------
Toda la lógica de negocio permanece en los servicios; las ventanas
actúan únicamente como capa de presentación.

Ventana principal:
  - Barra de menú con acceso a todas las funcionalidades
  - Barra de estado informativa en la parte inferior
  - Confirmación antes de cerrar el sistema

Paneles implementados:
  - Gestión de películas (con filtro de búsqueda y eliminación)
  - Gestión de directores
  - Gestión de actores (con asociación a películas)
  - Gestión de funciones presenciales y streaming
  - Venta de entradas presenciales y streaming (con alta de espectador)
  - Abonos y reservas (con alta de espectador)
  - Gestión de espectadores
  - Gestión de salas
  - Gestión de ediciones
  - Gestión de secciones: alta de sección con su premio y asociación
    de películas a cada sección
  - Evaluaciones y ganadores por sección
  - Voto del público y Premio del Público

Funcionalidades adicionales implementadas:
  - Barra de estado informativa (JLabel en la parte inferior)
  - Confirmaciones antes de operaciones críticas (cerrar, eliminar)
  - Búsqueda y filtro en tiempo real sobre tablas (JTextField + TableRowSorter)
  - Visualización de ocupación mediante JProgressBar en panel de funciones
  - Guardado de datos en hilo secundario (Thread) para no bloquear la UI
  - Visualización de butacas ocupadas al vender entrada presencial


DECISIONES DE DISEÑO
--------------------
Persistencia:
  Se persisten en archivos todos los datos del sistema (ver sección
  PERSISTENCIA). Las relaciones se guardan por clave: las personas por
  email, las películas por título y las funciones, entradas y abonos por
  su código. Los archivos usan formato CSV con comillas, así que un
  nombre o título con coma no rompe la línea. Si una línea es inválida,
  se anota el error y se sigue cargando el resto; al terminar se
  muestran todos los errores juntos.

Identidad:
  Las personas se identifican por email (puede haber dos personas con el
  mismo nombre). Películas por título, salas por nombre, ediciones por
  año y funciones, entradas y abonos por código. Todas definen
  equals/hashCode.

Butacas:
  Las butacas son números del 1 a la capacidad de la sala, envueltos en
  el record Butaca. Las entradas streaming no tienen butaca
  (Optional vacío).

Fechas:
  Todo lo que depende de la fecha usa un java.time.Clock que se recibe
  desde Main, así se puede probar con un reloj fijo. Una función "ya
  pasó" cuando llegó su fecha y hora de inicio: desde ese momento no se
  venden entradas, no se reserva y no se cancelan reservas.

Ganadores:
  El panel de evaluaciones calcula y muestra los ganadores por sección
  automáticamente al cargarse y se actualiza cada vez que se agrega
  una nueva evaluación.

Thread de guardado:
  Al guardar datos (desde el menú Archivo o al cerrar), primero se toma
  una copia de los datos en el hilo de la interfaz y después esa copia
  se escribe en un hilo secundario, para que la interfaz no se congele
  y el hilo no lea listas que la interfaz puede estar modificando. Los
  guardados se escriben de a uno y en orden. Cada archivo se escribe en
  un temporal y después se reemplaza, así un error no deja el archivo a
  medias. Al salir, la ventana espera a que termine el guardado y, si
  falló, muestra el error y pregunta si salir igual.


EXCEPCIONES PERSONALIZADAS
--------------------------
  Unchecked (extienden RuntimeException):
    FestivalException (abstracta, base de todas)
      ├─ DatoInvalidoException       → dato obligatorio o fuera de rango
      ├─ EntidadDuplicadaException   → ya existe (email, título, evaluación...)
      ├─ EntidadNoEncontradaException
      └─ ReglaNegocioException       → operación no permitida
           ├─ ButacaOcupadaException
           ├─ CapacidadExcedidaException
           ├─ FuncionNoDisponibleException
           ├─ AbonoInvalidoException
           └─ VotoInvalidoException

  Checked (extiende Exception):
    PersistenciaException → errores al leer o escribir archivos
                            (trae la lista de todos los errores)

  La interfaz atrapa FestivalException (no RuntimeException ni Exception),
  así un error de programación no se disfraza de mensaje para el usuario.


PERSISTENCIA
------------
Archivos persistidos (CSV; los campos con coma o comillas van entre comillas):
  salas.txt                → nombre, capacidad
  directores.txt           → nombre, email, nacionalidad
  actores.txt              → nombre, email, nacionalidad
  jurados.txt              → nombre, email, nacionalidad
  espectadores.txt         → nombre, email, nacionalidad
  peliculas.txt            → título, género, duración, email del director
  reparto.txt              → título, email del actor
  evaluaciones.txt         → email del jurado, título, puntaje
  ediciones.txt            → año, ciudad, fecha inicio, fecha fin
  secciones.txt            → año, tipo, premio, descripción del premio
  secciones_peliculas.txt  → año, tipo de sección, título
  funciones.txt            → código, PRESENCIAL, título, fecha y hora, sala
                             código, STREAMING, título, fecha y hora, enlace, capacidad
  abonos.txt               → código, tipo, email, tarifa, precio, días (d1|d2|d3)
  entradas.txt             → código, código de función, email, precio,
                             butaca (vacía en streaming), código de abono (vacío
                             si es suelta)
  votos.txt                → email del espectador, título, puntaje


DATOS PRECARGADOS
-----------------
Al iniciar el sistema por primera vez se cargan desde archivos:
  - 5 salas
  - 11 directores
  - 10 actores
  - 10 espectadores
  - 5 jurados
  - 17 películas
  - 42 evaluaciones

Y se inicializan vía inicializacion/InicializadorDatos:
  - 3 ediciones: 2024-Madrid, 2025-Paris y la edición actual en
    Buenos Aires, que va desde hoy hasta 30 días después (como las
    funciones tienen que caer dentro de su edición, la edición actual
    se calcula a partir de la fecha del día)
  - 4 secciones por edición con sus premios
  - Asociaciones película-sección (las películas con funciones compiten
    en la edición actual)
  - 20 funciones (14 presenciales y 6 streaming)
  - Algunas entradas vendidas para demostrar ocupación
  - 3 abonos con reservas y 18 votos del público

Al guardar se escriben todos los archivos; en los arranques siguientes
se usa lo guardado y la demostración ya no se vuelve a generar. Para
volver a la demostración inicial, borrar ediciones.txt (y los demás
archivos nuevos).


CÓMO EJECUTAR EL SISTEMA
-------------------------
1. Compilar el proyecto con IntelliJ IDEA
2. Colocar los archivos .txt en la carpeta raíz del proyecto
3. Ejecutar la clase app.Main
4. El sistema cargará los datos automáticamente y mostrará
   la ventana principal
5. Al cerrar, el sistema preguntará si desea guardar los datos


FUNCIONES Y ENTRADAS STREAMING
------------------------------
Además de las funciones presenciales, el sistema permite funciones
streaming.

  - Funcion (abstract) → FuncionPresencial y FuncionStreaming
  - Entrada (abstract) → EntradaPresencial y EntradaStreaming

FuncionStreaming incorpora: enlace, capacidad virtual y espectadores
conectados. EntradaStreaming genera un enlace de acceso virtual.

Se modela con herencia y polimorfismo: el método calcularOcupacion()
se comporta de manera diferente en cada subclase.


FUNCIONALIDADES AGREGADAS EN ESTE PROYECTO
------------------------------------------
1) Abonos, precios y descuentos (menú Entradas > Abonos)

  Precios:
    Las entradas y los abonos ahora tienen precio. Al vender se elige
    una tarifa (enum TipoTarifa):
      GENERAL (sin descuento), ESTUDIANTE (30%), JUBILADO (40%)
    Las entradas sueltas compradas con 7 días o más de anticipación
    tienen un 10% adicional de descuento.

    Precios base:  entrada presencial $8000 | entrada streaming $5000
                   abono 3 días $20000 | abono full $45000
                   abono streaming $25000

  Tipos de abono (Abono abstract):
    - AbonoTresDias   → el espectador elige 3 días con funciones
                        presenciales y puede ver todo lo que quiera
                        esos días.
    - AbonoFull       → todas las funciones presenciales del festival.
    - AbonoStreaming  → todas las funciones streaming.
    Cada subclase redefine cubre(Funcion), que indica si el abono
    permite entrar a una función (polimorfismo).

  Reservas con abono:
    Con un abono se reserva lugar en una función sin volver a pagar.
    La reserva es una Entrada con precio 0 asociada al abono.
    Validaciones: el abono debe cubrir la función, la función no puede
    haber comenzado, dos reservas del mismo abono no pueden superponerse
    en horario (según la duración de cada película), y se validan butaca
    y capacidad como en la venta normal.
    Las reservas se pueden cancelar (se libera la butaca o el lugar
    virtual). Las entradas sueltas no se cancelan.

2) Voto del público (menú Evaluaciones > Voto del público)

  - Se elige la edición y el espectador; la pantalla ofrece solo las
    películas de esa edición que el espectador vio y todavía no votó.
  - Un espectador puede votar de 1 a 5 estrellas una película para la
    que tiene entrada (comprada o reservada con abono).
  - Cada espectador vota una sola vez cada película.
  - Premio del Público: por cada edición, la película (de sus
    secciones) con mejor promedio de votos del público. Se calcula en
    Edicion.determinarGanadoraPublico(), aparte del premio del jurado.

  Nuevas clases: TipoTarifa, Abono, AbonoTresDias, AbonoFull,
  AbonoStreaming, VotoPublico, AbonoInvalidoException,
  VotoInvalidoException, PanelAbonos, PanelVotoPublico.
