package modelo;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Festival {
    private String nombre;
    private List<Edicion> ediciones;

    public Festival(String nombre) {
        this.nombre = nombre;
        this.ediciones = new ArrayList<>();
    }

    public void agregarEdicion(Edicion e) {
        // CORRECCION I8: el año repetido lo controla el festival, que tiene la lista de ediciones.
        if (existeEdicion(e.getAnio())) {
            throw new EntidadDuplicadaException("Ya existe la edición " + e.getAnio() + ".");
        }
        ediciones.add(e);
    }

    public boolean existeEdicion(int anio) {
        for (Edicion e : ediciones) {
            if (e.getAnio() == anio) return true;
        }
        return false;
    }

    // CORRECCION S5: si no existe lanza una excepción en lugar de devolver null.
    public Edicion buscarEdicion(int anio) {
        for (Edicion e : ediciones) {
            if (e.getAnio() == anio) return e;
        }
        throw new EntidadNoEncontradaException("Edicion", "año " + anio);
    }

    public boolean compiteEnAlgunaEdicion(Pelicula pelicula) {
        for (Edicion e : ediciones) {
            if (e.incluye(pelicula)) return true;
        }
        return false;
    }

    // CORRECCION S5: antes devolvía null. Consultar compiteEnAlgunaEdicion() antes de llamarlo.
    public Edicion buscarEdicionDe(Pelicula pelicula) {
        for (Edicion e : ediciones) {
            if (e.incluye(pelicula)) return e;
        }
        throw new EntidadNoEncontradaException("Edicion", "la película " + pelicula.getTitulo() + " no compite en ninguna edición");
    }

    public void quitarPelicula(Pelicula pelicula) {
        for (Edicion e : ediciones) {
            e.quitarDeTodasLasSecciones(pelicula);
        }
    }

    public String getNombre() {
        return nombre;
    }
    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Edicion> getEdiciones() {
        return Collections.unmodifiableList(ediciones);
    }

    @Override
    public String toString() {
        return "Festival: " + nombre;
    }
}
