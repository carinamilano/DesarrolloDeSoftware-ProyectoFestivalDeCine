package servicios;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import modelo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* CORRECCION S2: directores, actores, jurados y espectadores se gestionan en un solo servicio.*/
public class PersonaServicio {
    private List<Director> directores;
    private List<Actor> actores;
    private List<Jurado> jurados;
    private List<Espectador> espectadores;

    public PersonaServicio() {
        this.directores = new ArrayList<>();
        this.actores = new ArrayList<>();
        this.jurados = new ArrayList<>();
        this.espectadores = new ArrayList<>();
    }

    public Director registrarDirector(String nombre, String email, String nacionalidad) {
        Director d = new Director(nombre, email, nacionalidad);
        validarEmailLibre(directores, d.getEmail(), "director");
        directores.add(d);
        return d;
    }

    public Actor registrarActor(String nombre, String email, String nacionalidad) {
        Actor a = new Actor(nombre, email, nacionalidad);
        validarEmailLibre(actores, a.getEmail(), "actor");
        actores.add(a);
        return a;
    }

    public Jurado registrarJurado(String nombre, String email, String nacionalidad) {
        Jurado j = new Jurado(nombre, email, nacionalidad);
        validarEmailLibre(jurados, j.getEmail(), "jurado");
        jurados.add(j);
        return j;
    }

    public Espectador registrarEspectador(String nombre, String email, String nacionalidad) {
        Espectador e = new Espectador(nombre, email, nacionalidad);
        validarEmailLibre(espectadores, e.getEmail(), "espectador");
        espectadores.add(e);
        return e;
    }

    // CORRECCION I8: no puede haber dos personas del mismo rol con el mismo email.
    private void validarEmailLibre(List<? extends Persona> personas, String email, String rol) {
        for (Persona p : personas) {
            if (p.tieneEmail(email)) {
                throw new EntidadDuplicadaException("Ya existe un " + rol + " con el email " + email + ".");
            }
        }
    }

    // CORRECCION I10: las búsquedas son por email; antes eran por nombre y con homónimos devolvían el primero.
    public Director buscarDirector(String email) {
        return buscarPorEmail(directores, email, "Director");
    }

    public Actor buscarActor(String email) {
        return buscarPorEmail(actores, email, "Actor");
    }

    public Jurado buscarJurado(String email) {
        return buscarPorEmail(jurados, email, "Jurado");
    }

    public Espectador buscarEspectador(String email) {
        return buscarPorEmail(espectadores, email, "Espectador");
    }

    private <T extends Persona> T buscarPorEmail(List<T> personas, String email, String entidad) {
        for (T p : personas) {
            if (p.tieneEmail(email)) return p;
        }
        throw new EntidadNoEncontradaException(entidad, "email: " + email);
    }

    // CORRECCION I13: las listas no se pueden modificar desde afuera.
    public List<Director> getDirectores() { return Collections.unmodifiableList(directores); }
    public List<Actor> getActores() { return Collections.unmodifiableList(actores); }
    public List<Jurado> getJurados() { return Collections.unmodifiableList(jurados); }
    public List<Espectador> getEspectadores() { return Collections.unmodifiableList(espectadores); }
}
