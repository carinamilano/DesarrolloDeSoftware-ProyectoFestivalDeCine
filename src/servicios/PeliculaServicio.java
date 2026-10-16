package servicios;

import excepciones.FestivalException;
import modelo.*;
import repositorio.Repositorio;

import java.util.List;

public class PeliculaServicio {
    private final Repositorio<Pelicula> peliculas;
    private final PersonaServicio personas;
    private final Festival festival;

    public PeliculaServicio(Repositorio<Pelicula> peliculas, PersonaServicio personas, Festival festival) {
        this.peliculas = peliculas;
        this.personas = personas;
        this.festival = festival;
    }

    public Pelicula registrarPelicula(String titulo, Genero genero, int duracion, String nombreDirector) {
        if (peliculas.existe(pelicula -> pelicula.seTitula(titulo))) {
            throw new FestivalException("Ya existe la película " + titulo + ".");
        }
        Pelicula pelicula = new Pelicula(titulo, genero, duracion, personas.buscarDirector(nombreDirector));
        peliculas.agregar(pelicula);
        return pelicula;
    }

    public void asociarActorAPelicula(String nombreActor, String tituloPelicula) {
        Actor actor = personas.buscarActor(nombreActor);
        buscarPelicula(tituloPelicula).agregarActor(actor);
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
