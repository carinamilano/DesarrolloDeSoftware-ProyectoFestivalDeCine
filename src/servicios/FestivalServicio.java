package servicios;

import excepciones.FestivalException;
import modelo.*;
import excepciones.EntidadNoEncontradaException;

import java.time.LocalDate;
import java.util.List;

public class FestivalServicio {
    private Festival festival;
    private List<Sala> salas;

    public FestivalServicio(Festival festival, List<Sala> salas) {
        this.festival = festival;
        this.salas = salas;
    }

    public void registrarEdicion(int anio, String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        if (anio < 2000) {
            throw new FestivalException("El año debe ser mayor a 2000.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new FestivalException("La fecha fin debe ser posterior a la fecha inicio.");
        }
        if (festival.buscarEdicion(anio) != null) {
            throw new FestivalException("Ya existe la edición " + anio + ".");
        }
        Edicion edicion = new Edicion(anio, ciudad, fechaInicio, fechaFin);
        festival.agregarEdicion(edicion);
    }

    public void registrarSeccion(int anioEdicion, TipoSeccion tipo, Premio premio) {
        Edicion edicion = buscarEdicion(anioEdicion);
        if (edicion.buscarSeccion(tipo) != null) {
            throw new FestivalException("La edición " + anioEdicion + " ya tiene la sección " + tipo + ".");
        }
        Seccion seccion = new Seccion(tipo, premio);
        edicion.agregarSeccion(seccion);
    }

    public void registrarSeccion(int anioEdicion, TipoSeccion tipo, String nombrePremio, String descripcionPremio) {
        if (nombrePremio == null || nombrePremio.isBlank()) {
            throw new FestivalException("El nombre del premio es obligatorio.");
        }
        registrarSeccion(anioEdicion, tipo, new Premio(nombrePremio, descripcionPremio));
    }

    public void registrarSala(String nombre, int capacidad) {
        Sala sala = new Sala(nombre, capacidad);
        salas.add(sala);
    }

    public void asociarPeliculaASeccion(int anioEdicion, TipoSeccion tipo, Pelicula pelicula) {
        Seccion seccion = buscarSeccion(anioEdicion, tipo);
        if (seccion.getPeliculas().contains(pelicula)) {
            throw new FestivalException(pelicula.getTitulo() + " ya está en la sección " + tipo + ".");
        }
        // Una película compite en una sola edición (puede estar en varias secciones de esa edición)
        Edicion otra = buscarEdicionDePelicula(pelicula);
        if (otra != null && otra.getAnio() != anioEdicion) {
            throw new FestivalException(pelicula.getTitulo() + " ya compite en la edición " + otra.getAnio() + ".");
        }
        seccion.agregarPelicula(pelicula);
    }

    // Devuelve la edición en la que compite la película, o null si todavía no está en ninguna
    public Edicion buscarEdicionDePelicula(Pelicula pelicula) {
        for (Edicion e : festival.getEdiciones()) {
            for (Seccion s : e.getSecciones()) {
                if (s.getPeliculas().contains(pelicula)) return e;
            }
        }
        return null;
    }

    public void quitarPeliculaDeSeccion(int anioEdicion, TipoSeccion tipo, Pelicula pelicula) {
        Seccion seccion = buscarSeccion(anioEdicion, tipo);
        if (!seccion.getPeliculas().contains(pelicula)) {
            throw new FestivalException(pelicula.getTitulo() + " no está en la sección " + tipo + ".");
        }
        seccion.eliminarPelicula(pelicula);
    }

    public Edicion buscarEdicion(int anio) {
        Edicion edicion = festival.buscarEdicion(anio);
        if (edicion == null) {
            throw new EntidadNoEncontradaException("Edicion", "anio: " + anio);
        }
        return edicion;
    }

    public Seccion buscarSeccion(int anioEdicion, TipoSeccion tipo) {
        Edicion edicion = buscarEdicion(anioEdicion);
        Seccion seccion = edicion.buscarSeccion(tipo);
        if (seccion == null) {
            throw new EntidadNoEncontradaException("Seccion", "tipo: " + tipo);
        }
        return seccion;
    }

    public Sala buscarSala(String nombre) {
        for (Sala s : salas) {
            if (s.getNombre().equalsIgnoreCase(nombre)) return s;
        }
        throw new EntidadNoEncontradaException("Sala", "nombre: " + nombre);
    }

    public Festival getFestival() { return festival; }
    public List<Sala> getSalas() { return salas; }
    public List<Edicion> getEdiciones() { return festival.getEdiciones(); }
}