package modelo;

import excepciones.FestivalException;
import excepciones.VotoInvalidoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

public final class Pelicula {
    private final String titulo;
    private final Genero genero;
    private final int duracion;
    private final Director director;
    private final List<Actor> actores;
    private final List<Evaluacion> evaluaciones;
    private final List<Funcion> funciones;
    private final List<VotoPublico> votosPublico;

    public Pelicula(String titulo, Genero genero, int duracion, Director director) {
        if (duracion <= 0) {
            throw new FestivalException("La duración debe ser mayor a 0.");
        }
        this.titulo = Validar.texto(titulo, "El título es obligatorio.");
        this.genero = Validar.presente(genero, "El género es obligatorio.");
        this.duracion = duracion;
        this.director = Validar.presente(director, "El director es obligatorio.");
        this.actores = new ArrayList<>();
        this.evaluaciones = new ArrayList<>();
        this.funciones = new ArrayList<>();
        this.votosPublico = new ArrayList<>();
        director.agregarPelicula(this);
    }

    // Mantiene la asociación en los dos sentidos (película -> actor y actor -> película)
    public void agregarActor(Actor actor) {
        if (actores.contains(actor)) {
            throw new FestivalException(actor.getNombre() + " ya está en el reparto de " + titulo + ".");
        }
        actores.add(actor);
        actor.agregarPelicula(this);
    }

    public void agregarFuncion(Funcion funcion) {
        funciones.add(funcion);
    }

    // Creator: la película contiene sus evaluaciones, así que ella las crea
    public Evaluacion recibirEvaluacion(Jurado jurado, double puntaje) {
        Evaluacion evaluacion = new Evaluacion(puntaje, this, jurado);
        evaluaciones.add(evaluacion);
        jurado.registrarEvaluacion(evaluacion);
        return evaluacion;
    }

    // Solo vota quien tiene entrada para la película, y una sola vez
    public VotoPublico recibirVoto(Espectador espectador, int puntaje) {
        VotoPublico voto = new VotoPublico(puntaje, this, espectador);
        if (!espectador.tieneEntradaPara(this)) {
            throw new VotoInvalidoException(espectador.getNombre() + " no tiene entrada para " + titulo + ".");
        }
        if (fueVotadaPor(espectador)) {
            throw new VotoInvalidoException(espectador.getNombre() + " ya votó " + titulo + ".");
        }
        votosPublico.add(voto);
        return voto;
    }

    public boolean fueVotadaPor(Espectador espectador) {
        return votosPublico.stream().anyMatch(voto -> voto.esDe(espectador));
    }

    public double calcularPromedioJurado() {
        return promedio(evaluaciones, Evaluacion::getPuntaje);
    }

    public double calcularPromedioPublico() {
        return promedio(votosPublico, VotoPublico::getPuntaje);
    }

    private static <T> double promedio(List<T> elementos, ToDoubleFunction<T> puntaje) {
        return elementos.stream().mapToDouble(puntaje).average().orElse(0);
    }

    public boolean tieneEvaluaciones() {
        return !evaluaciones.isEmpty();
    }

    public boolean tieneVotos() {
        return !votosPublico.isEmpty();
    }

    public int cantidadVotos() {
        return votosPublico.size();
    }

    // Solo se puede eliminar una película sin funciones, evaluaciones ni votos, para no dejar datos inconsistentes
    public void validarEliminable() {
        if (!funciones.isEmpty()) {
            throw new FestivalException("No se puede eliminar " + titulo + ": tiene funciones programadas.");
        }
        if (tieneEvaluaciones() || tieneVotos()) {
            throw new FestivalException("No se puede eliminar " + titulo + ": tiene evaluaciones o votos registrados.");
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
        return Collections.unmodifiableList(evaluaciones);
    }
    public List<Funcion> getFunciones() {
        return Collections.unmodifiableList(funciones);
    }
    public List<VotoPublico> getVotosPublico() {
        return Collections.unmodifiableList(votosPublico);
    }

    @Override
    public String toString() {
        return titulo + " (" + genero + ", " + duracion + " min) - Dir: " + getNombreDirector();
    }
}
