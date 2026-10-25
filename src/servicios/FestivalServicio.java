package servicios;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.ReglaNegocioException;
import modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FestivalServicio {
    private Festival festival;
    private PeliculaServicio peliculaServicio;
    private List<Sala> salas;

    public FestivalServicio(Festival festival, PeliculaServicio peliculaServicio) {
        this.festival = festival;
        this.peliculaServicio = peliculaServicio;
        this.salas = new ArrayList<>();
    }

    // CORRECCION I9: las validaciones de año y fechas están en el constructor de Edicion
    public Edicion registrarEdicion(int anio, String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        Edicion edicion = new Edicion(anio, ciudad, fechaInicio, fechaFin);
        festival.agregarEdicion(edicion);
        return edicion;
    }

    public Seccion registrarSeccion(Edicion edicion, TipoSeccion tipo, String nombrePremio, String descripcionPremio) {
        Seccion seccion = new Seccion(tipo, new Premio(nombrePremio, descripcionPremio));
        edicion.agregarSeccion(seccion);
        return seccion;
    }

    public Sala registrarSala(String nombre, int capacidad) {
        Sala sala = new Sala(nombre, capacidad);
        // CORRECCION I8: antes solo lo controlaba PanelSalas.
        if (salas.contains(sala)) {
            throw new EntidadDuplicadaException("Ya existe la " + sala.getNombre() + ".");
        }
        salas.add(sala);
        return sala;
    }

    public void asociarPeliculaASeccion(Edicion edicion, TipoSeccion tipo, Pelicula pelicula) {
        Seccion seccion = edicion.buscarSeccion(tipo);
        if (festival.compiteEnAlgunaEdicion(pelicula)) {
            Edicion otra = festival.buscarEdicionDe(pelicula);
            if (!otra.equals(edicion)) {
                throw new ReglaNegocioException(pelicula.getTitulo() + " ya compite en la edición " + otra.getAnio() + ".");
            }
        }
        seccion.agregarPelicula(pelicula);
    }

    // CORRECCION I7: Edicion no deja quitar una película con funciones de su última sección.
    public void quitarPeliculaDeSeccion(Edicion edicion, TipoSeccion tipo, Pelicula pelicula) {
        edicion.quitarPeliculaDeSeccion(tipo, pelicula);
    }

    // CORRECCION S7: antes este filtro estaba en PanelSecciones.
    public List<Pelicula> peliculasAsociables(Edicion edicion, Seccion seccion) {
        List<Pelicula> asociables = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            boolean libre = !festival.compiteEnAlgunaEdicion(p) || festival.buscarEdicionDe(p).equals(edicion);
            if (libre && !seccion.contiene(p)) {
                asociables.add(p);
            }
        }
        return asociables;
    }

    // CORRECCION I6: solo se pueden programar funciones de películas que compiten en alguna edición.
    public List<Pelicula> peliculasQueCompiten() {
        List<Pelicula> compiten = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            if (festival.compiteEnAlgunaEdicion(p)) compiten.add(p);
        }
        return compiten;
    }

    public boolean compiteEnAlgunaEdicion(Pelicula pelicula) {
        return festival.compiteEnAlgunaEdicion(pelicula);
    }

    public Edicion buscarEdicionDe(Pelicula pelicula) {
        return festival.buscarEdicionDe(pelicula);
    }

    public Edicion buscarEdicion(int anio) {
        return festival.buscarEdicion(anio);
    }

    public Sala buscarSala(String nombre) {
        for (Sala s : salas) {
            if (s.getNombre().equalsIgnoreCase(nombre.trim())) return s;
        }
        throw new EntidadNoEncontradaException("Sala", "nombre: " + nombre);
    }

    public Festival getFestival() { return festival; }
    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Sala> getSalas() { return Collections.unmodifiableList(salas); }
    public List<Edicion> getEdiciones() { return festival.getEdiciones(); }
}
