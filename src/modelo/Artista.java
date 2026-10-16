package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Comportamiento común de Director y Actor (antes estaba duplicado en las dos clases)
public abstract class Artista extends Persona {
    private final List<Pelicula> peliculas;

    protected Artista(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.peliculas = new ArrayList<>();
    }

    void agregarPelicula(Pelicula pelicula) {
        if (!peliculas.contains(pelicula)) {
            peliculas.add(pelicula);
        }
    }

    void quitarPelicula(Pelicula pelicula) {
        peliculas.remove(pelicula);
    }

    public int cantidadPeliculas() {
        return peliculas.size();
    }

    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }
}
