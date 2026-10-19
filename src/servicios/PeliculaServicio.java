package servicios;

import excepciones.EntidadDuplicadaException;
import modelo.comun.Validar;
import modelo.festival.Festival;
import modelo.peliculas.Genero;
import modelo.peliculas.Pelicula;
import modelo.personas.Actor;
import modelo.personas.Director;
import repositorio.Repositorio;

import java.util.List;

public class PeliculaServicio {
    private final Repositorio<Pelicula> peliculas;
    private final Festival festival;

    public PeliculaServicio(Repositorio<Pelicula> peliculas, Festival festival) {
        this.peliculas = peliculas;
        this.festival = festival;
    }

    public Pelicula registrarPelicula(String titulo, Genero genero, int duracion, Director director) {
        if (peliculas.existe(pelicula -> pelicula.seTitula(titulo))) {
            throw new EntidadDuplicadaException("Ya existe la película " + titulo + ".");
        }
        Pelicula pelicula = new Pelicula(titulo, genero, duracion, director);
        peliculas.agregar(pelicula);
        return pelicula;
    }

    public void asociarActorAPelicula(Actor actor, Pelicula pelicula) {
        Validar.presente(pelicula, "La película es obligatoria.").agregarActor(actor);
    }

    // La película valida si se puede eliminar; se la quita de sus secciones, de su director y de sus actores
    public void eliminarPelicula(String titulo) {
        Pelicula pelicula = buscarPelicula(titulo);
        pelicula.validarEliminable();
        festival.quitarPelicula(pelicula);
        pelicula.desvincular();
        peliculas.quitar(pelicula);
    }

    public Pelicula buscarPelicula(String titulo) {
        return peliculas.obtener(pelicula -> pelicula.seTitula(titulo), "Pelicula", "titulo: " + titulo);
    }

    public List<Pelicula> getPeliculas() {
        return peliculas.listar();
    }
}
