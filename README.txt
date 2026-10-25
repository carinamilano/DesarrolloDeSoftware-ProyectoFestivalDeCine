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
    comun/        → Piezas compartidas: formulario de persona, lectura de
                    campos, combos, tablas y buscador
  app/            → Clase Main (punto de entrada)
  datos/          → Archivos de datos (fuera de src, en la raíz del proyecto)


CLASES DEL MODELO
-----------------
  - Festival, Edicion, Seccion, Premio
  - TipoSeccion (enum): COMPETENCIA_OFICIAL, CINE_INDEPENDIENTE,
    CORTOMETRAJE, NUEVOS_TALENTOS
  - Persona (abstract) → Director, Actor, Jurado, Espectador
  - Pelicula, Evaluacion
  - Genero (enum): DRAMA, COMEDIA, THRILLER, CIENCIA_FICCION, ...
  - Funcion (abstract) → FuncionPresencial, FuncionStreaming
  - Entrada (abstract) → EntradaPresencial, EntradaStreaming
  - Sala
  - Abono (abstract) → AbonoTresDias, AbonoFull, AbonoStreaming
  - TipoAbono (enum): TRES_DIAS, FULL, STREAMING (con su precio base)
  - TipoTarifa (enum): GENERAL, ESTUDIANTE, JUBILADO
  - VotoPublico

Las reglas del negocio están en el modelo: cada constructor valida sus
datos (un objeto nunca se crea en un estado inválido) y cada clase
controla sus propias reglas (por ejemplo, Pelicula no deja que un
jurado la evalúe dos veces y Edicion no deja programar funciones fuera
de sus fechas). Las funciones, entradas y abonos se identifican por
código (F-1, EP-2, ES-3, AB-4) y las personas por email.


SERVICIOS
---------
Cada servicio coordina un grupo de casos de uso: busca los objetos y
llama a los métodos del modelo. La lógica NO se resuelve en la UI ni
en el Main.

  PersonaServicio:
    Directores, actores, jurados y espectadores. No permite dos personas
    del mismo rol con el mismo email. Las búsquedas son por email.

  PeliculaServicio:
    Películas y su reparto. No permite títulos repetidos y solo elimina
    películas sin funciones, evaluaciones ni votos.

  FestivalServicio:
    Gestiona el festival, ediciones, secciones (con su premio) y salas.
    Asocia y quita películas de las secciones. No permite dos ediciones
    del mismo año, dos secciones del mismo tipo en una edición ni dos
    salas con el mismo nombre. Una película compite en una sola edición
    (puede estar en varias secciones de esa edición), así sus
    evaluaciones y votos cuentan solo para los premios de esa edición.

  FuncionServicio:
    Programa funciones presenciales y streaming. Solo a futuro, de
    películas que compiten en una edición y dentro de sus fechas; en las
    presenciales controla que la sala no esté ocupada (con la duración
    de la película).

  VentaServicio:
    Venta de entradas sueltas, precios y cancelación de reservas. No
    vende entradas de funciones que ya comenzaron.

  AbonoServicio:
    Compra de abonos y reservas con abono. Controla que el abono cubra
    la función y que no se superponga con otra reserva del mismo abono.

  EvaluacionServicio:
    Evaluaciones del jurado (0 a 10) y votos del público (1 a 5).

  InicializadorDatos:
    Precarga datos de ejemplo la primera vez que se abre el sistema.


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
  Se guardan todos los datos (ver PERSISTENCIA). Las relaciones entre
  objetos se guardan por clave: email para las personas, título para
  las películas y código para funciones, entradas y abonos.

  Al cargar, los datos se crean con los mismos métodos que usa la
  aplicación, así se vuelven a validar las reglas. Una línea con
  errores se saltea y, al final, se muestran todos los errores juntos
  (archivo y número de línea); el sistema abre igual.

  Cada archivo se escribe primero en un .tmp y después reemplaza al
  anterior: si algo falla a mitad de camino, el archivo anterior queda
  intacto.

Butacas:
  Las butacas no se modelan como objetos independientes sino como
  números enteros del 1 a la capacidad de la sala, simplificando
  el modelo sin perder funcionalidad.

Ganadores:
  El panel de evaluaciones calcula y muestra los ganadores por sección
  automáticamente al cargarse y se actualiza cada vez que se agrega
  una nueva evaluación.

Thread de guardado:
  Al guardar desde el menú Archivo, el texto de los archivos se arma
  en el hilo de la interfaz y se escribe en un hilo secundario, para
  que la interfaz no se congele. Mientras guarda, "Guardar datos"
  queda deshabilitado. El resultado se informa en la barra de estado.
  Al cerrar, el sistema espera a que termine el guardado y, si falla,
  muestra el error y permite no salir.


EXCEPCIONES PERSONALIZADAS
--------------------------
  Unchecked (extienden RuntimeException):
    FestivalException (abstracta)
      ├── DatoInvalidoException        falta un dato o está fuera de rango
      ├── EntidadDuplicadaException    ya existe (email, título, sala...)
      ├── EntidadNoEncontradaException no se encontró lo buscado
      └── ReglaNegocioException        una regla del festival no lo permite
            ├── AbonoInvalidoException
            ├── ButacaOcupadaException
            ├── CapacidadExcedidaException
            ├── FuncionNoDisponibleException
            └── VotoInvalidoException

  Checked (extienden Exception):
    PersistenciaException  → errores al leer o escribir archivos
                             (trae la lista de errores)
    CampoInvalidoException → formato inválido en un campo de un
                             formulario (en ui/comun)


PERSISTENCIA
------------
Los archivos están en la carpeta datos/ de la raíz del proyecto, en
UTF-8. Los campos se separan con ";" (si un texto tiene ";" o comillas,
va entre comillas).

  salas.txt               → nombre;capacidad
  directores.txt          → nombre;email;nacionalidad
  actores.txt             → nombre;email;nacionalidad
  jurados.txt             → nombre;email;nacionalidad
  espectadores.txt        → nombre;email;nacionalidad
  peliculas.txt           → titulo;GENERO;duracion;emailDirector
  reparto.txt             → titulo;emailActor
  ediciones.txt           → anio;ciudad;fechaInicio;fechaFin
  secciones.txt           → anio;TIPO;premio;descripcion
  secciones_peliculas.txt → anio;TIPO;titulo
  funciones.txt           → codigo;PRESENCIAL;titulo;fecha;hora;sala
                            codigo;STREAMING;titulo;fecha;hora;enlace;capacidad
  evaluaciones.txt        → emailJurado;titulo;puntaje
  abonos.txt              → codigo;TIPO;emailEspectador;TARIFA;precio;
                            fechaCompra;dias (solo el abono de 3 días)
  entradas.txt            → codigo;codigoFuncion;emailEspectador;precio;
                            codigoAbono (vacío si es suelta);butaca
                            (vacía si es streaming)
  votos.txt               → emailEspectador;titulo;puntaje


DATOS PRECARGADOS
-----------------
La carpeta datos/ trae:
  - 5 salas
  - 11 directores
  - 10 actores
  - 10 espectadores
  - 5 jurados
  - 17 películas
  - 42 evaluaciones

La primera vez (cuando todavía no hay ediciones guardadas) se agregan
vía InicializadorDatos:
  - 3 ediciones: Madrid y Paris (los dos años anteriores) y Buenos
    Aires (el año actual, desde hoy y por 30 días)
  - 4 secciones por edición con sus premios
  - Asociaciones película-sección y reparto
  - 20 funciones (14 presenciales y 6 streaming) dentro de la edición
    actual
  - Algunas entradas vendidas para demostrar ocupación
  - 3 abonos con reservas y 18 votos del público
Al guardar, todo queda en datos/ y la demo no vuelve a correr.


CÓMO EJECUTAR EL SISTEMA
-------------------------
1. Compilar el proyecto con IntelliJ IDEA
2. Ejecutar desde la carpeta raíz del proyecto (ahí está datos/)
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
    haber comenzado, no puede haber dos reservas del mismo abono que se
    superpongan (según la duración de cada película), y se validan
    butaca y capacidad como en la venta normal.
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
