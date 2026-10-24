package modelo;

import java.util.ArrayList;
import java.util.List;

public class Actor extends Persona {
    private List<Pelicula> peliculas;

    public Actor(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.peliculas = new ArrayList<>();
    }

    public void agregarPelicula(Pelicula p) {
        peliculas.add(p);
    }

    public void quitarPelicula(Pelicula p) {
        peliculas.remove(p);
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    @Override
    public String toString() {
        return "Actor: " + getNombre();
    }
}