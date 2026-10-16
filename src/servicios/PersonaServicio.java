package servicios;

import excepciones.FestivalException;
import modelo.*;
import repositorio.Repositorio;

import java.util.List;

// Alta y búsqueda de las personas del sistema (antes repartido entre PeliculaServicio y EntradaServicio)
public class PersonaServicio {
    private final Repositorio<Director> directores;
    private final Repositorio<Actor> actores;
    private final Repositorio<Jurado> jurados;
    private final Repositorio<Espectador> espectadores;

    public PersonaServicio(Repositorio<Director> directores, Repositorio<Actor> actores,
                           Repositorio<Jurado> jurados, Repositorio<Espectador> espectadores) {
        this.directores = directores;
        this.actores = actores;
        this.jurados = jurados;
        this.espectadores = espectadores;
    }

    public Director registrarDirector(String nombre, String email, String nacionalidad) {
        return registrar(directores, new Director(nombre, email, nacionalidad));
    }

    public Actor registrarActor(String nombre, String email, String nacionalidad) {
        return registrar(actores, new Actor(nombre, email, nacionalidad));
    }

    public Jurado registrarJurado(String nombre, String email, String nacionalidad) {
        return registrar(jurados, new Jurado(nombre, email, nacionalidad));
    }

    public Espectador registrarEspectador(String nombre, String email, String nacionalidad) {
        return registrar(espectadores, new Espectador(nombre, email, nacionalidad));
    }

    // Las personas se buscan por nombre, así que no puede haber dos con el mismo nombre y rol
    private <T extends Persona> T registrar(Repositorio<T> repositorio, T persona) {
        if (repositorio.existe(otra -> otra.seLlama(persona.getNombre()))) {
            throw new FestivalException("Ya existe " + persona + ".");
        }
        repositorio.agregar(persona);
        return persona;
    }

    public Director buscarDirector(String nombre) {
        return buscar(directores, nombre, "Director");
    }

    public Actor buscarActor(String nombre) {
        return buscar(actores, nombre, "Actor");
    }

    public Jurado buscarJurado(String nombre) {
        return buscar(jurados, nombre, "Jurado");
    }

    public Espectador buscarEspectador(String nombre) {
        return buscar(espectadores, nombre, "Espectador");
    }

    private <T extends Persona> T buscar(Repositorio<T> repositorio, String nombre, String entidad) {
        return repositorio.obtener(persona -> persona.seLlama(nombre), entidad, "nombre: " + nombre);
    }

    public List<Director> getDirectores() { return directores.listar(); }
    public List<Actor> getActores() { return actores.listar(); }
    public List<Jurado> getJurados() { return jurados.listar(); }
    public List<Espectador> getEspectadores() { return espectadores.listar(); }
}
