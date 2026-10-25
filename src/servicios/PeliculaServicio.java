package servicios;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import modelo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* CORRECCION S2: este servicio quedó solo con las películas. Las personas pasaron a PersonaServicio
y las evaluaciones y votos a EvaluacionServicio.*/
public class PeliculaServicio {
    private Festival festival;
    private List<Pelicula> peliculas;

    public PeliculaServicio(Festival festival) {
        this.festival = festival;
        this.peliculas = new ArrayList<>();
    }

    // CORRECCION I10: recibe el Director elegido; antes recibía el nombre y lo volvía a buscar.
    public Pelicula registrarPelicula(String titulo, Genero genero, int duracion, Director director) {
        // CORRECCION I8: no se repiten títulos. Se controla antes de crearla porque la película se agrega a su director al construirse.
        if (existePelicula(titulo)) {
            throw new EntidadDuplicadaException("Ya existe la película " + titulo.trim() + ".");
        }
        Pelicula p = new Pelicula(titulo, genero, duracion, director);
        peliculas.add(p);
        return p;
    }

    // CORRECCION I8/I14: Pelicula.agregarActor controla el duplicado y actualiza los dos lados.
    public void asociarActorAPelicula(Actor actor, Pelicula pelicula) {
        pelicula.agregarActor(actor);
    }

    public void eliminarPelicula(Pelicula pelicula) {
        pelicula.desvincular();
        festival.quitarPelicula(pelicula);
        peliculas.remove(pelicula);
    }

    public boolean existePelicula(String titulo) {
        for (Pelicula p : peliculas) {
            if (p.seTitula(titulo)) return true;
        }
        return false;
    }

    public Pelicula buscarPelicula(String titulo) {
        for (Pelicula p : peliculas) {
            if (p.seTitula(titulo)) return p;
        }
        throw new EntidadNoEncontradaException("Pelicula", "titulo: " + titulo);
    }

    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }
}
