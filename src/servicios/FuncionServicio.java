package servicios;

import excepciones.EntidadNoEncontradaException;
import excepciones.FuncionNoDisponibleException;
import modelo.*;
import repositorio.Repositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Programación de funciones (antes era una de las cinco responsabilidades de EntradaServicio)
public class FuncionServicio {
    private final Repositorio<FuncionPresencial> presenciales;
    private final Repositorio<FuncionStreaming> streaming;

    public FuncionServicio(Repositorio<FuncionPresencial> presenciales, Repositorio<FuncionStreaming> streaming) {
        this.presenciales = presenciales;
        this.streaming = streaming;
    }

    public FuncionPresencial programarFuncionPresencial(Pelicula pelicula, Sala sala, LocalDateTime fechaHora) {
        FuncionPresencial funcion = new FuncionPresencial(pelicula, sala, fechaHora);
        validarQueNoPaso(funcion);
        if (presenciales.existe(otra -> otra.seSuperponeCon(funcion))) {
            throw new FuncionNoDisponibleException("ya existe una función en esa sala, fecha y horario.");
        }
        presenciales.agregar(funcion);
        pelicula.agregarFuncion(funcion);
        return funcion;
    }

    public FuncionStreaming programarFuncionStreaming(Pelicula pelicula, String enlace, int capacidad, LocalDateTime fechaHora) {
        FuncionStreaming funcion = new FuncionStreaming(pelicula, enlace, capacidad, fechaHora);
        validarQueNoPaso(funcion);
        streaming.agregar(funcion);
        pelicula.agregarFuncion(funcion);
        return funcion;
    }

    private void validarQueNoPaso(Funcion funcion) {
        if (funcion.yaPaso()) {
            throw new FuncionNoDisponibleException("no se puede programar una función en una fecha pasada.");
        }
    }

    // Busca por película, fecha Y horario: antes ignoraba el horario y devolvía la primera
    // función del día (por eso las entradas de las 20:00 se vendían para la función de la tarde)
    public Funcion buscarFuncion(String tituloPelicula, LocalDateTime fechaHora) {
        return getFunciones().stream()
                .filter(funcion -> funcion.corresponde(tituloPelicula, fechaHora))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Funcion", tituloPelicula + " " + fechaHora));
    }

    // Fechas (sin repetir y ordenadas) en las que hay funciones presenciales
    public List<LocalDate> getFechasConFuncionesPresenciales() {
        return presenciales.listar().stream().map(Funcion::getFecha).distinct().sorted().toList();
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
}
