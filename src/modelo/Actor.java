package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Actor extends Persona {
    private List<Pelicula> peliculas;

    public Actor(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.peliculas = new ArrayList<>();
    }

    // CORRECCION I14: solo los llama Pelicula, que mantiene los dos lados de la relación
    void agregarPelicula(Pelicula p) {
        peliculas.add(p);
    }

    void quitarPelicula(Pelicula p) {
        peliculas.remove(p);
    }

    // CORRECCION I13: la lista no se puede modificar desde afuera
    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }

    @Override
    public String toString() {
        return "Actor: " + getNombre();
    }
}
