package modelo.personas;

import excepciones.ReglaNegocioException;
import modelo.peliculas.Pelicula;

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

    // Lo llama Pelicula para mantener la asociación en los dos sentidos. Es público porque Pelicula
    // está en otro paquete; por eso solo acepta películas que ya tienen registrado a este artista
    public void agregarPelicula(Pelicula pelicula) {
        if (!pelicula.participa(this)) {
            throw new ReglaNegocioException(getNombre() + " no participa en " + pelicula.getTitulo() + ".");
        }
        if (!peliculas.contains(pelicula)) {
            peliculas.add(pelicula);
        }
    }

    public void quitarPelicula(Pelicula pelicula) {
        peliculas.remove(pelicula);
    }

    public int cantidadPeliculas() {
        return peliculas.size();
    }

    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }
}
