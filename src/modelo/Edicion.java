package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Edicion {
    private int anio;
    private String ciudad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Seccion> secciones;

    public Edicion(int anio, String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        this.anio = anio;
        this.ciudad = ciudad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.secciones = new ArrayList<>();
    }

    public void agregarSeccion(Seccion s) {
        secciones.add(s);
    }

    public Seccion buscarSeccion(TipoSeccion tipo) {
        for (Seccion s : secciones) {
            if (s.getTipo() == tipo) return s;
        }
        return null;
    }

    // Premio del Público: la película de la edición con mejor promedio de votos del público
    public Pelicula determinarGanadoraPublico() {
        Pelicula ganadora = null;
        for (Seccion s : secciones) {
            for (Pelicula p : s.getPeliculas()) {
                if (p.getVotosPublico().isEmpty()) continue;
                if (ganadora == null || p.calcularPromedioPublico() > ganadora.calcularPromedioPublico()) {
                    ganadora = p;
                }
            }
        }
        return ganadora;
    }

    public int getAnio() {
        return anio;
    }
    public String getCiudad() {
        return ciudad;
    }
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public List<Seccion> getSecciones() {
        return secciones;
    }

    @Override
    public String toString() {
        return "Edicion " + anio + " - " + ciudad;
    }
}