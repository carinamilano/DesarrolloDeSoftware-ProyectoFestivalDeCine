package persistencia;

import excepciones.DatoInvalidoException;
import excepciones.PersistenciaException;
import modelo.*;
import servicios.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Persistencia {

    // CORRECCION P11: los archivos van en una carpeta fija ("datos") y no sueltos en la carpeta de trabajo.
    public static final String CARPETA_DATOS = "datos";

    /* CORRECCIÓN P5: se guardan los 14 tipos de datos. Antes solo se guardaban los 7 primeros
    y lo que se cargaba de ediciones, funciones, entradas, abonos y votos se perdía al cerrar.
    Las referencias van por clave: email para personas, título para películas y código para
    funciones, entradas y abonos (CORRECCIÓN P6: antes director y jurado iban por nombre).*/
    private static final String SALAS = "salas.txt";
    private static final String DIRECTORES = "directores.txt";
    private static final String ACTORES = "actores.txt";
    private static final String JURADOS = "jurados.txt";
    private static final String ESPECTADORES = "espectadores.txt";
    private static final String PELICULAS = "peliculas.txt";
    private static final String REPARTO = "reparto.txt";
    private static final String EDICIONES = "ediciones.txt";
    private static final String SECCIONES = "secciones.txt";
    private static final String SECCIONES_PELICULAS = "secciones_peliculas.txt";
    private static final String FUNCIONES = "funciones.txt";
    private static final String EVALUACIONES = "evaluaciones.txt";
    private static final String ABONOS = "abonos.txt";
    private static final String ENTRADAS = "entradas.txt";
    private static final String VOTOS = "votos.txt";
    private static final String PRESENCIAL = "PRESENCIAL";
    private static final String STREAMING = "STREAMING";
    private final Path carpeta;
    private final PersonaServicio personaServicio;
    private final PeliculaServicio peliculaServicio;
    private final FestivalServicio festivalServicio;
    private final FuncionServicio funcionServicio;
    private final VentaServicio ventaServicio;
    private final AbonoServicio abonoServicio;
    private final EvaluacionServicio evaluacionServicio;
    private final List<String> erroresDeCarga;

    public Persistencia(String carpeta, PersonaServicio personaServicio, PeliculaServicio peliculaServicio, FestivalServicio festivalServicio, FuncionServicio funcionServicio, VentaServicio ventaServicio, AbonoServicio abonoServicio, EvaluacionServicio evaluacionServicio) {
        this.carpeta = Paths.get(carpeta);
        this.personaServicio = personaServicio;
        this.peliculaServicio = peliculaServicio;
        this.festivalServicio = festivalServicio;
        this.funcionServicio = funcionServicio;
        this.ventaServicio = ventaServicio;
        this.abonoServicio = abonoServicio;
        this.evaluacionServicio = evaluacionServicio;
        this.erroresDeCarga = new ArrayList<>();
    }

    public Path getCarpeta() {
        return carpeta.toAbsolutePath();
    }

    /* CORRECCIÓN P2/P3: una línea con errores (o que apunta a un dato que no existe) se anota con su
     archivo y número de línea y se sigue con la siguiente. Antes una línea mal escrita impedía abrir
     la aplicación y una película sin director se descartaba sin avisar.
     Los datos se crean con los mismos métodos que usa la aplicación, así se vuelven a validar las reglas.*/
    public void cargar() throws PersistenciaException {
        erroresDeCarga.clear();
        cargarSalas();
        cargarDirectores();
        cargarActores();
        cargarJurados();
        cargarEspectadores();
        cargarPeliculas();
        cargarReparto();
        cargarEdiciones();
        cargarSecciones();
        cargarSeccionesPeliculas();
        cargarFunciones();
        cargarEvaluaciones();
        cargarAbonos();
        cargarEntradas();
        cargarVotos();
        if (!erroresDeCarga.isEmpty()) {
            throw new PersistenciaException("Se encontraron " + erroresDeCarga.size()
                    + " errores al cargar los datos. Esas líneas no se cargaron.", erroresDeCarga);
        }
    }

    // CORRECCION P3: la ventana lo consulta para avisar, antes de guardar, que las líneas con errores se van a perder.
    public boolean huboErroresDeCarga() {
        return !erroresDeCarga.isEmpty();
    }

    private void cargarSalas() {
        for (Registro r : leer(SALAS)) {
            try {
                festivalServicio.registrarSala(r.texto(0), r.entero(1));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarDirectores() {
        for (Registro r : leer(DIRECTORES)) {
            try {
                personaServicio.registrarDirector(r.texto(0), r.texto(1), r.texto(2));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarActores() {
        for (Registro r : leer(ACTORES)) {
            try {
                personaServicio.registrarActor(r.texto(0), r.texto(1), r.texto(2));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarJurados() {
        for (Registro r : leer(JURADOS)) {
            try {
                personaServicio.registrarJurado(r.texto(0), r.texto(1), r.texto(2));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarEspectadores() {
        for (Registro r : leer(ESPECTADORES)) {
            try {
                personaServicio.registrarEspectador(r.texto(0), r.texto(1), r.texto(2));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarPeliculas() {
        for (Registro r : leer(PELICULAS)) {
            try {
                Director director = personaServicio.buscarDirector(r.texto(3));
                peliculaServicio.registrarPelicula(r.texto(0), r.enumerado(1, Genero.class), r.entero(2), director);
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarReparto() {
        for (Registro r : leer(REPARTO)) {
            try {
                Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(0));
                peliculaServicio.asociarActorAPelicula(personaServicio.buscarActor(r.texto(1)), pelicula);
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarEdiciones() {
        for (Registro r : leer(EDICIONES)) {
            try {
                festivalServicio.registrarEdicion(r.entero(0), r.texto(1), r.fecha(2), r.fecha(3));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarSecciones() {
        for (Registro r : leer(SECCIONES)) {
            try {
                Edicion edicion = festivalServicio.buscarEdicion(r.entero(0));
                festivalServicio.registrarSeccion(edicion, r.enumerado(1, TipoSeccion.class), r.texto(2), r.textoOpcional(3));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarSeccionesPeliculas() {
        for (Registro r : leer(SECCIONES_PELICULAS)) {
            try {
                Edicion edicion = festivalServicio.buscarEdicion(r.entero(0));
                Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(2));
                festivalServicio.asociarPeliculaASeccion(edicion, r.enumerado(1, TipoSeccion.class), pelicula);
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarFunciones() {
        for (Registro r : leer(FUNCIONES)) {
            try {
                String codigo = r.texto(0);
                String tipo = r.texto(1).toUpperCase();
                Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(2));
                if (tipo.equals(PRESENCIAL)) {
                    Sala sala = festivalServicio.buscarSala(r.texto(5));
                    funcionServicio.restaurarFuncionPresencial(codigo, pelicula, sala, r.fecha(3), r.hora(4));
                } else if (tipo.equals(STREAMING)) {
                    funcionServicio.restaurarFuncionStreaming(codigo, pelicula, r.texto(5), r.entero(6), r.fecha(3), r.hora(4));
                } else {
                    throw new DatoInvalidoException("tipo de función desconocido: " + tipo + ".");
                }
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    /* CORRECCION I3/I9/P13: las evaluaciones pasan por la misma validación que en la aplicación,
    así se rechazan las repetidas y los puntajes fuera de rango.*/
    private void cargarEvaluaciones() {
        for (Registro r : leer(EVALUACIONES)) {
            try {
                Jurado jurado = personaServicio.buscarJurado(r.texto(0));
                Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(1));
                evaluacionServicio.registrarEvaluacion(jurado, pelicula, r.decimal(2));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarAbonos() {
        for (Registro r : leer(ABONOS)) {
            try {
                Espectador espectador = personaServicio.buscarEspectador(r.texto(2));
                abonoServicio.restaurarAbono(r.texto(0), r.enumerado(1, TipoAbono.class), espectador,
                        r.enumerado(3, TipoTarifa.class), r.decimal(4), r.fecha(5), r.fechas(6));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarEntradas() {
        for (Registro r : leer(ENTRADAS)) {
            try {
                String codigo = r.texto(0);
                String codigoFuncion = r.texto(1);
                Espectador espectador = personaServicio.buscarEspectador(r.texto(2));
                double precio = r.decimal(3);
                Abono abono = r.textoOpcional(4).isEmpty() ? null : abonoServicio.buscarAbono(r.texto(4));
                if (!r.textoOpcional(5).isEmpty()) {
                    FuncionPresencial funcion = funcionServicio.buscarFuncionPresencial(codigoFuncion);
                    ventaServicio.restaurarEntradaPresencial(codigo, funcion, espectador, r.entero(5), precio, abono);
                } else {
                    FuncionStreaming funcion = funcionServicio.buscarFuncionStreaming(codigoFuncion);
                    ventaServicio.restaurarEntradaStreaming(codigo, funcion, espectador, precio, abono);
                }
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private void cargarVotos() {
        for (Registro r : leer(VOTOS)) {
            try {
                Espectador espectador = personaServicio.buscarEspectador(r.texto(0));
                Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(1));
                evaluacionServicio.registrarVotoPublico(espectador, pelicula, r.entero(2));
            } catch (RuntimeException e) {
                anotarError(r, e);
            }
        }
    }

    private List<Registro> leer(String archivo) {
        List<Registro> registros = new ArrayList<>();
        Path ruta = carpeta.resolve(archivo);
        if (!Files.exists(ruta)) return registros;
        List<String> lineas;
        try {
            lineas = ArchivoTexto.leerLineas(ruta);
        } catch (IOException e) {
            erroresDeCarga.add(archivo + ": no se pudo leer el archivo (" + e.getMessage() + ").");
            return registros;
        }
        for (int i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).isBlank()) continue;
            try {
                registros.add(new Registro(archivo, i + 1, lineas.get(i)));
            } catch (RuntimeException e) {
                erroresDeCarga.add(archivo + ", línea " + (i + 1) + ": " + e.getMessage());
            }
        }
        return registros;
    }

    private void anotarError(Registro r, RuntimeException e) {
        erroresDeCarga.add(r.ubicacion() + ": " + e.getMessage());
    }


    /* CORRECCION P9: arma el texto de todos los archivos. Se llama en el hilo de la interfaz y el hilo de
    guardado solo escribe estas líneas; antes el hilo recorría las listas mientras la UI las podía cambiar.*/
    public Map<String, List<String>> prepararGuardado() {
        Map<String, List<String>> archivos = new LinkedHashMap<>();
        archivos.put(SALAS, lineasSalas());
        archivos.put(DIRECTORES, lineasPersonas(personaServicio.getDirectores()));
        archivos.put(ACTORES, lineasPersonas(personaServicio.getActores()));
        archivos.put(JURADOS, lineasPersonas(personaServicio.getJurados()));
        archivos.put(ESPECTADORES, lineasPersonas(personaServicio.getEspectadores()));
        archivos.put(PELICULAS, lineasPeliculas());
        archivos.put(REPARTO, lineasReparto());
        archivos.put(EDICIONES, lineasEdiciones());
        archivos.put(SECCIONES, lineasSecciones());
        archivos.put(SECCIONES_PELICULAS, lineasSeccionesPeliculas());
        archivos.put(FUNCIONES, lineasFunciones());
        archivos.put(EVALUACIONES, lineasEvaluaciones());
        archivos.put(ABONOS, lineasAbonos());
        archivos.put(ENTRADAS, lineasEntradas());
        archivos.put(VOTOS, lineasVotos());
        return archivos;
    }

    /* CORRECCION P8/P10: cada archivo se escribe de forma segura (ver ArchivoTexto) y, si alguno falla,
    se informa cuál y por qué, sin cortar el guardado de los demás.*/
    public void escribir(Map<String, List<String>> archivos) throws PersistenciaException {
        List<String> errores = new ArrayList<>();
        for (Map.Entry<String, List<String>> archivo : archivos.entrySet()) {
            try {
                ArchivoTexto.escribirLineas(carpeta.resolve(archivo.getKey()), archivo.getValue());
            } catch (IOException e) {
                errores.add(archivo.getKey() + ": " + e.getMessage());
            }
        }
        if (!errores.isEmpty()) {
            throw new PersistenciaException("No se pudieron guardar " + errores.size() + " archivos.", errores);
        }
    }

    private List<String> lineasSalas() {
        List<String> lineas = new ArrayList<>();
        for (Sala s : festivalServicio.getSalas()) {
            lineas.add(FormatoLinea.unir(s.getNombre(), s.getCantidadButacas()));
        }
        return lineas;
    }

    private List<String> lineasPersonas(List<? extends Persona> personas) {
        List<String> lineas = new ArrayList<>();
        for (Persona p : personas) {
            lineas.add(FormatoLinea.unir(p.getNombre(), p.getEmail(), p.getNacionalidad()));
        }
        return lineas;
    }

    private List<String> lineasPeliculas() {
        List<String> lineas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            lineas.add(FormatoLinea.unir(p.getTitulo(), p.getGenero().name(), p.getDuracion(), p.getDirector().getEmail()));
        }
        return lineas;
    }

    private List<String> lineasReparto() {
        List<String> lineas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            for (Actor a : p.getActores()) {
                lineas.add(FormatoLinea.unir(p.getTitulo(), a.getEmail()));
            }
        }
        return lineas;
    }

    private List<String> lineasEdiciones() {
        List<String> lineas = new ArrayList<>();
        for (Edicion e : festivalServicio.getEdiciones()) {
            lineas.add(FormatoLinea.unir(e.getAnio(), e.getCiudad(), e.getFechaInicio(), e.getFechaFin()));
        }
        return lineas;
    }

    private List<String> lineasSecciones() {
        List<String> lineas = new ArrayList<>();
        for (Edicion e : festivalServicio.getEdiciones()) {
            for (Seccion s : e.getSecciones()) {
                lineas.add(FormatoLinea.unir(e.getAnio(), s.getTipo().name(),
                        s.getPremio().getNombre(), s.getPremio().getDescripcion()));
            }
        }
        return lineas;
    }

    private List<String> lineasSeccionesPeliculas() {
        List<String> lineas = new ArrayList<>();
        for (Edicion e : festivalServicio.getEdiciones()) {
            for (Seccion s : e.getSecciones()) {
                for (Pelicula p : s.getPeliculas()) {
                    lineas.add(FormatoLinea.unir(e.getAnio(), s.getTipo().name(), p.getTitulo()));
                }
            }
        }
        return lineas;
    }

    private List<String> lineasFunciones() {
        List<String> lineas = new ArrayList<>();
        for (FuncionPresencial f : funcionServicio.getFuncionesPresenciales()) {
            lineas.add(FormatoLinea.unir(f.getCodigo(), PRESENCIAL, f.getPelicula().getTitulo(),
                    f.getFecha(), f.getHorario(), f.getSala().getNombre()));
        }
        for (FuncionStreaming f : funcionServicio.getFuncionesStreaming()) {
            lineas.add(FormatoLinea.unir(f.getCodigo(), STREAMING, f.getPelicula().getTitulo(),
                    f.getFecha(), f.getHorario(), f.getEnlace(), f.getCapacidadVirtual()));
        }
        return lineas;
    }

    private List<String> lineasEvaluaciones() {
        List<String> lineas = new ArrayList<>();
        for (Evaluacion e : evaluacionServicio.getEvaluaciones()) {
            lineas.add(FormatoLinea.unir(e.getJurado().getEmail(), e.getPelicula().getTitulo(), e.getPuntaje()));
        }
        return lineas;
    }

    private List<String> lineasAbonos() {
        List<String> lineas = new ArrayList<>();
        for (Abono a : abonoServicio.getAbonos()) {
            lineas.add(FormatoLinea.unir(a.getCodigo(), a.getTipo().name(), a.getEspectador().getEmail(),
                    a.getTarifa().name(), a.getPrecio(), a.getFechaCompra(), unirFechas(a.getDiasElegidos())));
        }
        return lineas;
    }

    private List<String> lineasEntradas() {
        List<String> lineas = new ArrayList<>();
        for (EntradaPresencial e : ventaServicio.getEntradasPresenciales()) {
            lineas.add(FormatoLinea.unir(e.getCodigo(), e.getFuncion().getCodigo(), e.getEspectador().getEmail(),
                    e.getPrecio(), codigoAbono(e), e.getButaca()));
        }
        for (EntradaStreaming e : ventaServicio.getEntradasStreaming()) {
            lineas.add(FormatoLinea.unir(e.getCodigo(), e.getFuncion().getCodigo(), e.getEspectador().getEmail(),
                    e.getPrecio(), codigoAbono(e), ""));
        }
        return lineas;
    }

    private List<String> lineasVotos() {
        List<String> lineas = new ArrayList<>();
        for (VotoPublico v : evaluacionServicio.getVotosPublico()) {
            lineas.add(FormatoLinea.unir(v.getEspectador().getEmail(), v.getPelicula().getTitulo(), v.getPuntaje()));
        }
        return lineas;
    }

    private String codigoAbono(Entrada e) {
        return e.esReservaDeAbono() ? e.getAbono().getCodigo() : "";
    }

    private String unirFechas(List<LocalDate> fechas) {
        StringBuilder texto = new StringBuilder();
        for (LocalDate f : fechas) {
            if (texto.length() > 0) texto.append(",");
            texto.append(f);
        }
        return texto.toString();
    }
}
