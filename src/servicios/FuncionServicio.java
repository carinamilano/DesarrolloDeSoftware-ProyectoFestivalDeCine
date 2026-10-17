package servicios;

import excepciones.EntidadNoEncontradaException;
import excepciones.FuncionNoDisponibleException;
import modelo.comun.Numerador;
import modelo.comun.Validar;
import modelo.festival.Edicion;
import modelo.festival.Festival;
import modelo.festival.Sala;
import modelo.funciones.Funcion;
import modelo.funciones.FuncionPresencial;
import modelo.funciones.FuncionStreaming;
import modelo.peliculas.Pelicula;
import repositorio.Repositorio;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Programación de funciones
public class FuncionServicio {
    private final Repositorio<FuncionPresencial> presenciales;
    private final Repositorio<FuncionStreaming> streaming;
    private final Festival festival;
    private final Numerador numerador;
    private final Clock reloj;

    public FuncionServicio(Repositorio<FuncionPresencial> presenciales, Repositorio<FuncionStreaming> streaming,
                           Festival festival, Numerador numerador, Clock reloj) {
        this.presenciales = presenciales;
        this.streaming = streaming;
        this.festival = festival;
        this.numerador = numerador;
        this.reloj = reloj;
    }

    public FuncionPresencial programarFuncionPresencial(Pelicula pelicula, Sala sala, LocalDateTime fechaHora) {
        validarProgramacion(pelicula, fechaHora);
        Validar.presente(sala, "La sala es obligatoria.");
        // La sala queda ocupada desde el inicio hasta que termina la película (antes solo se comparaba la hora de inicio)
        LocalDateTime fin = pelicula.terminaSiEmpiezaA(fechaHora);
        presenciales.buscar(otra -> otra.ocupaLaSala(sala, fechaHora, fin)).ifPresent(otra -> {
            throw new FuncionNoDisponibleException("la " + sala.getNombre() + " está ocupada por "
                    + otra.getTituloPelicula() + " de " + otra.getHorario() + " a " + otra.getFin().toLocalTime() + ".");
        });
        FuncionPresencial funcion = new FuncionPresencial(nuevoCodigo(), pelicula, sala, fechaHora, reloj);
        presenciales.agregar(funcion);
        pelicula.agregarFuncion(funcion);
        return funcion;
    }

    public FuncionStreaming programarFuncionStreaming(Pelicula pelicula, String enlace, int capacidad, LocalDateTime fechaHora) {
        validarProgramacion(pelicula, fechaHora);
        FuncionStreaming funcion = new FuncionStreaming(nuevoCodigo(), pelicula, enlace, capacidad, fechaHora, reloj);
        streaming.agregar(funcion);
        pelicula.agregarFuncion(funcion);
        return funcion;
    }

    // Una función se programa a futuro, para una película que compite en una edición y dentro de las fechas
    // de esa edición (antes se podía programar cualquier película en cualquier fecha)
    private void validarProgramacion(Pelicula pelicula, LocalDateTime fechaHora) {
        Validar.presente(pelicula, "La película es obligatoria.");
        Validar.presente(fechaHora, "La fecha y el horario son obligatorios.");
        if (!fechaHora.isAfter(LocalDateTime.now(reloj))) {
            throw new FuncionNoDisponibleException("no se puede programar una función en una fecha u horario que ya pasó.");
        }
        Edicion edicion = festival.buscarEdicionDe(pelicula).orElseThrow(() -> new FuncionNoDisponibleException(
                pelicula.getTitulo() + " no compite en ninguna edición del festival."));
        edicion.validarFechaDeFuncion(fechaHora.toLocalDate());
    }

    private String nuevoCodigo() {
        return "F-" + numerador.siguiente();
    }

    // Películas a las que se les puede programar una función: compiten en una edición que no terminó
    public List<Pelicula> getPeliculasProgramables() {
        LocalDate hoy = LocalDate.now(reloj);
        return festival.getEdiciones().stream()
                .filter(edicion -> !edicion.yaTermino(hoy))
                .flatMap(edicion -> edicion.getPeliculas().stream())
                .toList();
    }

    public Edicion buscarEdicionDe(Pelicula pelicula) {
        return festival.buscarEdicionDe(pelicula)
                .orElseThrow(() -> new EntidadNoEncontradaException("Edicion", "de " + pelicula.getTitulo()));
    }

    // Busca por película, fecha y horario
    public Funcion buscarFuncion(String tituloPelicula, LocalDateTime fechaHora) {
        return getFunciones().stream()
                .filter(funcion -> funcion.corresponde(tituloPelicula, fechaHora))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Funcion", tituloPelicula + " " + fechaHora));
    }

    // Fechas (sin repetir, ordenadas y que no pasaron) en las que hay funciones presenciales
    public List<LocalDate> getFechasConFuncionesPresenciales() {
        LocalDate hoy = LocalDate.now(reloj);
        return presenciales.listar().stream().map(Funcion::getFecha)
                .filter(fecha -> !fecha.isBefore(hoy))
                .distinct().sorted().toList();
    }

    public boolean hayFuncionesPresencialesEl(LocalDate fecha) {
        return presenciales.existe(funcion -> funcion.getFecha().equals(fecha));
    }

    public List<Funcion> getFunciones() {
        List<Funcion> funciones = new ArrayList<>(presenciales.listar());
        funciones.addAll(streaming.listar());
        return List.copyOf(funciones);
    }

    public List<FuncionPresencial> getFuncionesPresenciales() {
        return presenciales.listar();
    }

    public List<FuncionStreaming> getFuncionesStreaming() {
        return streaming.listar();
    }

    // Las que todavía se pueden vender: no comenzaron
    public List<FuncionPresencial> getFuncionesPresencialesEnVenta() {
        return presenciales.listar().stream().filter(funcion -> !funcion.yaPaso()).toList();
    }

    public List<FuncionStreaming> getFuncionesStreamingEnVenta() {
        return streaming.listar().stream().filter(funcion -> !funcion.yaPaso()).toList();
    }
}
