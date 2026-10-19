package modelo.peliculas;

import excepciones.DatoInvalidoException;
import excepciones.EntidadDuplicadaException;
import excepciones.ReglaNegocioException;
import excepciones.VotoInvalidoException;
import modelo.comun.Validar;
import modelo.funciones.Funcion;
import modelo.personas.Actor;
import modelo.personas.Artista;
import modelo.personas.Director;
import modelo.personas.Espectador;
import modelo.personas.Jurado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class Pelicula {
    private final String titulo;
    private final Genero genero;
    private final int duracion;
    private final Director director;
    private final List<Actor> actores;
    private final List<Funcion> funciones;
    private final Calificaciones calificaciones;

    public Pelicula(String titulo, Genero genero, int duracion, Director director) {
        if (duracion <= 0) {
            throw new DatoInvalidoException("La duración debe ser mayor a 0.");
        }
        this.titulo = Validar.texto(titulo, "El título es obligatorio.");
        this.genero = Validar.presente(genero, "El género es obligatorio.");
        this.duracion = duracion;
        this.director = Validar.presente(director, "El director es obligatorio.");
        this.actores = new ArrayList<>();
        this.funciones = new ArrayList<>();
        this.calificaciones = new Calificaciones();
        director.agregarPelicula(this);
    }

    // Mantiene la asociación en los dos sentidos (película -> actor y actor -> película)
    public void agregarActor(Actor actor) {
        Validar.presente(actor, "El actor es obligatorio.");
        if (actores.contains(actor)) {
            throw new EntidadDuplicadaException(actor.getNombre() + " ya está en el reparto de " + titulo + ".");
        }
        actores.add(actor);
        actor.agregarPelicula(this);
    }

    public boolean participa(Artista artista) {
        return director.equals(artista) || actores.contains(artista);
    }

    public void agregarFuncion(Funcion funcion) {
        if (!funcion.esDe(this)) {
            throw new ReglaNegocioException("La función no es de " + titulo + ".");
        }
        funciones.add(funcion);
    }

    // Hora en que termina una función de esta película que empieza a la hora indicada
    public LocalDateTime terminaSiEmpiezaA(LocalDateTime inicio) {
        return inicio.plusMinutes(duracion);
    }

    // Creator: la película contiene sus evaluaciones, así que ella las crea.
    // Cada jurado evalúa una película una sola vez (antes no se controlaba)
    public Evaluacion recibirEvaluacion(Jurado jurado, double puntaje) {
        Validar.presente(jurado, "El jurado es obligatorio.");
        if (fueEvaluadaPor(jurado)) {
            throw new EntidadDuplicadaException(jurado.getNombre() + " ya evaluó " + titulo + ".");
        }
        Evaluacion evaluacion = new Evaluacion(puntaje, this, jurado);
        calificaciones.agregar(evaluacion);
        jurado.registrarEvaluacion(evaluacion);
        return evaluacion;
    }

    // Solo vota quien tiene entrada para la película, y una sola vez.
    // Primero se valida y después se crea el voto (antes el voto se creaba antes de validar)
    public VotoPublico recibirVoto(Espectador espectador, int puntaje) {
        Validar.presente(espectador, "El espectador es obligatorio.");
        if (!espectador.tieneEntradaPara(this)) {
            throw new VotoInvalidoException(espectador.getNombre() + " no tiene entrada para " + titulo + ".");
        }
        if (fueVotadaPor(espectador)) {
            throw new VotoInvalidoException(espectador.getNombre() + " ya votó " + titulo + ".");
        }
        VotoPublico voto = new VotoPublico(puntaje, this, espectador);
        calificaciones.agregar(voto);
        return voto;
    }

    public boolean fueEvaluadaPor(Jurado jurado) {
        return calificaciones.fueEvaluadaPor(jurado);
    }

    public boolean fueVotadaPor(Espectador espectador) {
        return calificaciones.fueVotadaPor(espectador);
    }

    public double calcularPromedioJurado() {
        return calificaciones.promedioJurado();
    }

    public double calcularPromedioPublico() {
        return calificaciones.promedioPublico();
    }

    public boolean tieneEvaluaciones() {
        return calificaciones.tieneEvaluaciones();
    }

    public boolean tieneVotos() {
        return calificaciones.tieneVotos();
    }

    public int cantidadVotos() {
        return calificaciones.cantidadVotos();
    }

    public boolean tieneFunciones() {
        return !funciones.isEmpty();
    }

    // Solo se puede eliminar una película sin funciones, evaluaciones ni votos, para no dejar datos inconsistentes
    public void validarEliminable() {
        if (tieneFunciones()) {
            throw new ReglaNegocioException("No se puede eliminar " + titulo + ": tiene funciones programadas.");
        }
        if (tieneEvaluaciones() || tieneVotos()) {
            throw new ReglaNegocioException("No se puede eliminar " + titulo + ": tiene evaluaciones o votos registrados.");
        }
    }

    // Al eliminarla se la quita de su director y de sus actores
    public void desvincular() {
        director.quitarPelicula(this);
        actores.forEach(actor -> actor.quitarPelicula(this));
    }

    public boolean seTitula(String otroTitulo) {
        return otroTitulo != null && titulo.equalsIgnoreCase(otroTitulo.trim());
    }

    public String getTitulo() {
        return titulo;
    }
    public Genero getGenero() {
        return genero;
    }
    public int getDuracion() {
        return duracion;
    }
    public Director getDirector() {
        return director;
    }
    public String getNombreDirector() {
        return director.getNombre();
    }
    public List<Actor> getActores() {
        return Collections.unmodifiableList(actores);
    }
    public List<Evaluacion> getEvaluaciones() {
        return calificaciones.getEvaluaciones();
    }
    public List<Funcion> getFunciones() {
        return Collections.unmodifiableList(funciones);
    }
    public List<VotoPublico> getVotosPublico() {
        return calificaciones.getVotos();
    }

    // Identidad: el título (el sistema no permite dos películas con el mismo título)
    @Override
    public boolean equals(Object otro) {
        return otro instanceof Pelicula pelicula && titulo.equalsIgnoreCase(pelicula.titulo);
    }

    @Override
    public int hashCode() {
        return titulo.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return titulo + " (" + genero + ", " + duracion + " min) - Dir: " + getNombreDirector();
    }
}
