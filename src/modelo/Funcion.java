package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Funcion {
    private LocalDate fecha;
    private LocalTime horario;
    private Pelicula pelicula;

    public Funcion(LocalDate fecha, LocalTime horario, Pelicula pelicula) {
        this.fecha = fecha;
        this.horario = horario;
        this.pelicula = pelicula;
    }

    public abstract boolean verificarDisponibilidad();
    public abstract double calcularOcupacion();

    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHorario() {
        return horario;
    }
    public Pelicula getPelicula() {
        return pelicula;
    }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " - " + fecha + " " + horario;
    }
}