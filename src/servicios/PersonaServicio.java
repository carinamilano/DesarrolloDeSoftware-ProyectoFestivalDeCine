package servicios;

import excepciones.EntidadDuplicadaException;
import modelo.personas.Actor;
import modelo.personas.Director;
import modelo.personas.Espectador;
import modelo.personas.Jurado;
import modelo.personas.Persona;
import repositorio.Repositorio;

import java.util.List;

// Alta y búsqueda de las personas del sistema
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

    // Las personas se identifican por email: puede haber dos con el mismo nombre, pero no con el mismo email
    private <T extends Persona> T registrar(Repositorio<T> repositorio, T persona) {
        if (repositorio.existe(persona::equals)) {
            throw new EntidadDuplicadaException("Ya hay alguien registrado con el email " + persona.getEmail() + ".");
        }
        repositorio.agregar(persona);
        return persona;
    }

    public Director buscarDirector(String email) {
        return buscar(directores, email, "Director");
    }

    public Actor buscarActor(String email) {
        return buscar(actores, email, "Actor");
    }

    public Jurado buscarJurado(String email) {
        return buscar(jurados, email, "Jurado");
    }

    public Espectador buscarEspectador(String email) {
        return buscar(espectadores, email, "Espectador");
    }

    private <T extends Persona> T buscar(Repositorio<T> repositorio, String email, String entidad) {
        return repositorio.obtener(persona -> persona.tieneEmail(email), entidad, "email: " + email);
    }

    public List<Director> getDirectores() { return directores.listar(); }
    public List<Actor> getActores() { return actores.listar(); }
    public List<Jurado> getJurados() { return jurados.listar(); }
    public List<Espectador> getEspectadores() { return espectadores.listar(); }
}
