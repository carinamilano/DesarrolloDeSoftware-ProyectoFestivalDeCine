========================================================
SISTEMA DE GESTIÓN DE FESTIVAL DE CINE
Programación Orientada a Objetos - UADE
Alumno: Milano, Carina Victoria - LU: 1227609
========================================================

DESCRIPCIÓN DEL SISTEMA
------------------------
Sistema de gestión para un Festival Internacional de Cine desarrollado en Java.
Permite administrar ediciones del festival, películas, funciones, venta de
entradas presenciales y streaming, evaluaciones del jurado y determinación
de películas ganadoras por sección.

Esta tercera etapa incorpora una interfaz gráfica desarrollada con Java Swing,
reemplazando completamente la interacción por consola de la Etapa 2.


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
    entradas/     → Paneles de venta de entradas y espectadores
    evaluaciones/ → Panel de evaluaciones y ganadores
    festival/     → Paneles de ediciones y salas
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


SERVICIOS
---------
La lógica de negocio está separada en tres servicios, respetando el
principio de separación de responsabilidades. La lógica NO se resuelve
en la UI ni en el Main.

  FestivalServicio:
    Gestiona el festival, ediciones, secciones y salas.

  PeliculaServicio:
    Gestiona películas, directores, actores, jurados y evaluaciones.
    Calcula promedios y determina películas ganadoras por sección.

  EntradaServicio:
    Gestiona funciones presenciales y streaming, espectadores y
    venta de entradas. Valida disponibilidad, butacas y capacidades.

  InicializadorDatos:
    Precarga datos iniciales al arrancar el sistema.


INTERFAZ GRÁFICA - ETAPA 3
---------------------------
La interfaz reemplaza completamente la interacción por consola.
Toda la lógica de negocio permanece en los servicios; las ventanas
actúan únicamente como capa de presentación.

Ventana principal:
  - Barra de menú con acceso a todas las funcionalidades
  - Barra de estado informativa en la parte inferior
  - Confirmación antes de cerrar el sistema

Paneles implementados:
  - Gestión de películas (con filtro de búsqueda)
  - Gestión de directores
  - Gestión de actores (con asociación a películas)
  - Gestión de funciones presenciales y streaming
  - Venta de entradas presenciales y streaming
  - Gestión de espectadores
  - Gestión de salas
  - Gestión de ediciones
  - Evaluaciones y ganadores por sección

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

  En la Etapa 2 se tomó la decisión de no persistir ediciones, secciones,
  funciones y entradas en archivos, dado que guardar sus relaciones entre
  objetos requeriría una complejidad mayor a la del alcance del curso.
  Estos datos se inicializan automáticamente al arrancar mediante
  InicializadorDatos.

  En la Etapa 3 se incorporaron en la interfaz gráfica los formularios
  para gestionar ediciones, secciones y funciones, demostrando el uso
  correcto de componentes Swing y validaciones. Sin embargo, por la misma
  razón de diseño, los datos ingresados a través de estos formularios
  estarán disponibles solo durante la sesión actual y no se guardarán
  en archivo al cerrar el sistema.

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
    EntidadNoEncontradaException

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
  - 10 directores
  - 10 actores
  - 10 espectadores
  - 5 jurados
  - 10 películas
  - 24 evaluaciones

Y se inicializan via InicializadorDatos:
  - 3 ediciones (2024-Madrid, 2025-Paris, 2026-Buenos Aires)
  - 4 secciones por edición con sus premios
  - Asociaciones película-sección
  - 20 funciones (14 presenciales y 6 streaming)
  - Algunas entradas vendidas para demostrar ocupación


CÓMO EJECUTAR EL SISTEMA
-------------------------
1. Compilar el proyecto con IntelliJ IDEA
2. Colocar los archivos .txt en la carpeta raíz del proyecto
3. Ejecutar la clase app.Main
4. El sistema cargará los datos automáticamente y mostrará
   la ventana principal
5. Al cerrar, el sistema preguntará si desea guardar los datos


EXTENSIÓN ORIGINAL - FUNCIONES STREAMING
-----------------------------------------
Se incorporó la posibilidad de realizar funciones streaming
además de las funciones presenciales.

  - Funcion (abstract) → FuncionPresencial y FuncionStreaming
  - Entrada (abstract) → EntradaPresencial y EntradaStreaming

FuncionStreaming incorpora: enlace, capacidad virtual y espectadores
conectados. EntradaStreaming genera un enlace de acceso virtual.

La extensión se integró utilizando herencia y polimorfismo.
El método calcularOcupacion() se comporta de manera diferente en
cada subclase, demostrando polimorfismo.

========================================================

NUEVAS FUNCIONALIDADES - ABONOS Y VOTO DEL PÚBLICO
--------------------------------------------------
1) Abonos del festival y precios (menú Entradas > Abonos)

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

  - Un espectador puede votar de 1 a 5 estrellas una película para la
    que tiene entrada (comprada o reservada con abono).
  - Cada espectador vota una sola vez cada película.
  - Premio del Público: por cada edición, la película (de sus
    secciones) con mejor promedio de votos del público. Se calcula en
    Edicion.determinarGanadoraPublico(), aparte del premio del jurado.

  Nuevas clases: TipoTarifa, Abono, AbonoTresDias, AbonoFull,
  AbonoStreaming, VotoPublico, AbonoInvalidoException,
  VotoInvalidoException, PanelAbonos, PanelVotoPublico.

  Igual que las funciones y las entradas, los abonos y los votos no se
  persisten en archivo: se precargan con InicializadorDatos (3 abonos
  con reservas y 18 votos del público).
