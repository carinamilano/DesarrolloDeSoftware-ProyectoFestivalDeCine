package persistencia;

// Un archivo por tipo de dato. Se guardan los 14 tipos: antes solo se guardaban 7 y las ediciones,
// secciones, reparto, funciones, entradas, abonos y votos se perdían al cerrar.
// Las personas se referencian por email, las películas por título y funciones, entradas y abonos por código
enum ArchivoDatos {
    SALAS("salas.txt", "salas"),                                    // nombre, capacidad
    DIRECTORES("directores.txt", "directores"),                     // nombre, email, nacionalidad
    ACTORES("actores.txt", "actores"),                              // nombre, email, nacionalidad
    JURADOS("jurados.txt", "jurados"),                              // nombre, email, nacionalidad
    ESPECTADORES("espectadores.txt", "espectadores"),               // nombre, email, nacionalidad
    PELICULAS("peliculas.txt", "películas"),                        // título, género, duración, email del director
    REPARTO("reparto.txt", "reparto"),                              // título, email del actor
    EVALUACIONES("evaluaciones.txt", "evaluaciones"),               // email del jurado, título, puntaje
    EDICIONES("ediciones.txt", "ediciones"),                        // año, ciudad, fecha inicio, fecha fin
    SECCIONES("secciones.txt", "secciones"),                        // año, tipo, premio, descripción del premio
    PELICULAS_POR_SECCION("secciones_peliculas.txt", "películas por sección"), // año, tipo, título
    FUNCIONES("funciones.txt", "funciones"),                        // código, PRESENCIAL, título, fecha y hora, sala
                                                                    // código, STREAMING, título, fecha y hora, enlace, capacidad
    ABONOS("abonos.txt", "abonos"),                                 // código, tipo, email, tarifa, precio, días (solo 3 días: d1|d2|d3)
    ENTRADAS("entradas.txt", "entradas"),                           // código, código de función, email, precio, butaca, código de abono
    VOTOS("votos.txt", "votos del público");                        // email del espectador, título, puntaje

    static final String PRESENCIAL = "PRESENCIAL";
    static final String STREAMING = "STREAMING";

    private final ArchivoTexto archivo;

    ArchivoDatos(String nombreArchivo, String entidad) {
        this.archivo = new ArchivoTexto(nombreArchivo, entidad);
    }

    ArchivoTexto archivo() {
        return archivo;
    }
}
