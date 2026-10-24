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

  modelo/         → Clases que representan las entidades del dominio
  excepciones/    → Excepciones personalizadas del sistema
  servicios/      → Lógica de negocio del sistema
  persistencia/   → Lectura y escritura de archivos
  ui/             → Interfaz gráfica (Swing)
    peliculas/    → Paneles de gestión de películas, directores y actores
    funciones/    → Panel de gestión de funciones
    entradas/     → Paneles de venta de entradas, abonos y espectadores
    evaluaciones/ → Paneles de evaluaciones, ganadores y voto del público
    festival/     → Paneles de ediciones, secciones y salas
  app/            → Clase Main (punto de entrada)


CLASES DEL MODELO
-----------------
  - Festival, Edicion, Seccion, Premio
  - TipoSeccion (enum): COMPETENCIA_OFICIAL, CINE_INDEPENDIENTE,
    CORTOMETRAJE, NUEVOS_TALENTOS
  - Persona (abstract) → Director, Actor, Jurado, Espectador
  - Pelicula, Evaluacion
  - Funcion (abstract) → FuncionPresencial, FuncionStreaming
  - Entrada (abstract) → EntradaPresencial, EntradaStreaming
  - Sala
  - Abono (abstract) → AbonoTresDias, AbonoFull, AbonoStreaming
  - TipoTarifa (enum): GENERAL, ESTUDIANTE, JUBILADO
  - VotoPublico


SERVICIOS
---------
La lógica de negocio está separada en tres servicios, respetando el
principio de separación de responsabilidades. La lógica NO se resuelve
en la UI ni en el Main.

  FestivalServicio:
    Gestiona el festival, ediciones, secciones (con su premio) y salas.
    Asocia y quita películas de las secciones. No permite dos ediciones
    del mismo año ni dos secciones del mismo tipo en una edición.
    Una película compite en una sola edición (puede estar en varias
    secciones de esa edición), así sus evaluaciones y votos cuentan solo
    para los premios de esa edición.

  PeliculaServicio:
    Gestiona películas, directores, actores, jurados, evaluaciones y
    votos del público. Calcula promedios y determina películas ganadoras.
    Valida el puntaje del jurado (0 a 10) y controla que una película
    solo pueda eliminarse si no tiene funciones, evaluaciones ni votos.

  EntradaServicio:
    Gestiona funciones presenciales y streaming, espectadores, venta de
    entradas, abonos y reservas. Calcula precios y valida disponibilidad,
    butacas y capacidades.

  InicializadorDatos:
    Precarga datos iniciales al arrancar el sistema.


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
  Se persisten en archivos: salas, directores, actores, jurados,
  espectadores, películas y evaluaciones.

  Se decidió no persistir en archivos las ediciones, secciones,
  funciones, entradas, abonos y votos, dado que guardar sus relaciones
  entre objetos requeriría una complejidad mayor a la del alcance del
  curso. Estos datos se inicializan automáticamente al arrancar
  mediante InicializadorDatos, y lo que se cargue desde la interfaz
  para estas entidades está disponible solo durante la sesión actual.

Butacas:
  Las butacas no se modelan como objetos independientes sino como
  números enteros del 1 a la capacidad de la sala, simplificando
  el modelo sin perder funcionalidad.

Ganadores:
  El panel de evaluaciones calcula y muestra los ganadores por sección
  automáticamente al cargarse y se actualiza cada vez que se agrega
  una nueva evaluación.

Thread de guardado:
  Al guardar datos (desde el menú Archivo o al cerrar), el proceso
  se ejecuta en un hilo secundario para que la interfaz gráfica no
  se congele durante la escritura de archivos. El resultado se
  informa en la barra de estado.


EXCEPCIONES PERSONALIZADAS
--------------------------
  Unchecked (extienden RuntimeException):
    FestivalException, ButacaOcupadaException,
    CapacidadExcedidaException, FuncionNoDisponibleException,
    EntidadNoEncontradaException, AbonoInvalidoException,
    VotoInvalidoException

  Checked (extiende Exception):
    PersistenciaException → errores al leer o escribir archivos


PERSISTENCIA
------------
Archivos persistidos:
  salas.txt        → nombre, capacidad
  directores.txt   → nombre, email, nacionalidad
  actores.txt      → nombre, email, nacionalidad
  jurados.txt      → nombre, email, nacionalidad
  espectadores.txt → nombre, email, nacionalidad
  peliculas.txt    → titulo, genero, duracion, director
  evaluaciones.txt → jurado, pelicula, puntaje


DATOS PRECARGADOS
-----------------
Al iniciar el sistema se cargan automáticamente desde archivos:
  - 5 salas
  - 11 directores
  - 10 actores
  - 10 espectadores
  - 5 jurados
  - 17 películas
  - 43 evaluaciones

Y se inicializan via InicializadorDatos:
  - 3 ediciones (2024-Madrid, 2025-Paris, 2026-Buenos Aires)
  - 4 secciones por edición con sus premios
  - Asociaciones película-sección
  - 20 funciones (14 presenciales y 6 streaming)
  - Algunas entradas vendidas para demostrar ocupación
  - 3 abonos con reservas y 18 votos del público


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
    haber pasado, no puede haber dos reservas a la misma fecha y hora,
    y se validan butaca y capacidad como en la venta normal.
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
