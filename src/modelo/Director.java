package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Director extends Persona {
    private List<Pelicula> peliculasDirigidas;

    public Director(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.peliculasDirigidas = new ArrayList<>();
    }

    // CORRECCION I14: solo los llama Pelicula, que mantiene los dos lados de la relación.
    void agregarPelicula(Pelicula p) {
        peliculasDirigidas.add(p);
    }

    void quitarPelicula(Pelicula p) {
        peliculasDirigidas.remove(p);
    }

    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Pelicula> getPeliculasDirigidas() {
        return Collections.unmodifiableList(peliculasDirigidas);
    }

    @Override
    public String toString() {
        return "Director: " + getNombre();
    }
}
