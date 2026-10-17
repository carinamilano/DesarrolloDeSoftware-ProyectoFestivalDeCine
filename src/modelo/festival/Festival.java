package modelo.festival;

import excepciones.EntidadDuplicadaException;
import modelo.comun.Validar;
import modelo.peliculas.Pelicula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Festival {
    private final String nombre;
    private final List<Edicion> ediciones;

    public Festival(String nombre) {
        this.nombre = Validar.texto(nombre, "El nombre del festival es obligatorio.");
        this.ediciones = new ArrayList<>();
    }

    public void agregarEdicion(Edicion edicion) {
        Validar.presente(edicion, "La edición es obligatoria.");
        if (ediciones.contains(edicion)) {
            throw new EntidadDuplicadaException("Ya existe la edición " + edicion.getAnio() + ".");
        }
        ediciones.add(edicion);
    }

    public Optional<Edicion> buscarEdicion(int anio) {
        return ediciones.stream().filter(edicion -> edicion.getAnio() == anio).findFirst();
    }

    // Una película compite en una sola edición: devuelve cuál, si ya está en alguna
    public Optional<Edicion> buscarEdicionDe(Pelicula pelicula) {
        return ediciones.stream().filter(edicion -> edicion.incluye(pelicula)).findFirst();
    }

    public void quitarPelicula(Pelicula pelicula) {
        ediciones.forEach(edicion -> edicion.quitarPelicula(pelicula));
    }

    public boolean tieneEdiciones() {
        return !ediciones.isEmpty();
    }

    public String getNombre() {
        return nombre;
    }
    public List<Edicion> getEdiciones() {
        return Collections.unmodifiableList(ediciones);
    }

    @Override
    public String toString() {
        return "Festival: " + nombre;
    }
}
