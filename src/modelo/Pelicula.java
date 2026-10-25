package modelo;

import excepciones.DatoInvalidoException;
import excepciones.EntidadDuplicadaException;
import excepciones.ReglaNegocioException;
import excepciones.VotoInvalidoException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Pelicula {
    private String titulo;
    // CORRECCION S4: el género es un enum, ya no texto libre.
    private Genero genero;
    private int duracion;
    private Director director;
    private List<Actor> actores;
    private List<Evaluacion> evaluaciones;
    private List<Funcion> funciones;
    private List<VotoPublico> votosPublico;

    public Pelicula(String titulo, Genero genero, int duracion, Director director) {
        // CORRECCION I9: se valida acá; antes la duración solo la controlaba PanelPeliculas.
        this.titulo = Validar.texto(titulo, "El título es obligatorio.");
        this.genero = Validar.presente(genero, "El género es obligatorio.");
        if (duracion <= 0) {
            throw new DatoInvalidoException("La duración debe ser mayor a 0.");
        }
        this.duracion = duracion;
        this.director = Validar.presente(director, "El director es obligatorio.");
        this.actores = new ArrayList<>();
        this.evaluaciones = new ArrayList<>();
        this.funciones = new ArrayList<>();
        this.votosPublico = new ArrayList<>();
        // CORRECCION I14: la película se agrega a su director al crearse; el servicio ya no lo hace a mano.
        director.agregarPelicula(this);
    }

    public void agregarActor(Actor a) {
        Validar.presente(a, "El actor es obligatorio.");
        // CORRECCION I8: un actor no se puede asociar dos veces a la misma película.
        if (actores.contains(a)) {
            throw new EntidadDuplicadaException(a.getNombre() + " ya está en el reparto de " + titulo + ".");
        }
        actores.add(a);
        // CORRECCION I14: este metodo actualiza los dos lados de la relación.
        a.agregarPelicula(this);
    }

    // CORRECCION I3: un jurado evalúa cada película una sola vez.
    // CORRECCION I14: crea la evaluación y la agrega a la película y al jurado en un solo paso.
    public Evaluacion registrarEvaluacion(Jurado jurado, double puntaje) {
        Validar.presente(jurado, "El jurado es obligatorio.");
        if (fueEvaluadaPor(jurado)) {
            throw new EntidadDuplicadaException(jurado.getNombre() + " ya evaluó " + titulo + ".");
        }
        Evaluacion evaluacion = new Evaluacion(puntaje, this, jurado);
        evaluaciones.add(evaluacion);
        jurado.agregarEvaluacion(evaluacion);
        return evaluacion;
    }

    public boolean fueEvaluadaPor(Jurado jurado) {
        for (Evaluacion e : evaluaciones) {
            if (e.getJurado().equals(jurado)) return true;
        }
        return false;
    }

    public VotoPublico registrarVoto(Espectador espectador, int puntaje) {
        Validar.presente(espectador, "El espectador es obligatorio.");
        if (!espectador.tieneEntradaPara(this)) {
            throw new VotoInvalidoException(espectador.getNombre() + " no tiene entrada para " + titulo + ".");
        }
        if (fueVotadaPor(espectador)) {
            throw new VotoInvalidoException(espectador.getNombre() + " ya votó " + titulo + ".");
        }
        VotoPublico voto = new VotoPublico(puntaje, this, espectador);
        votosPublico.add(voto);
        return voto;
    }

    // CORRECCION I14: solo lo llama Funcion.vincular.
    void agregarFuncion(Funcion f) {
        if (!funciones.contains(f)) funciones.add(f);
    }

    public boolean fueVotadaPor(Espectador espectador) {
        for (VotoPublico v : votosPublico) {
            // CORRECCION S15: compara con equals (por email) en lugar de ==.
            if (v.getEspectador().equals(espectador)) return true;
        }
        return false;
    }

    // CORRECCION I4: con la duración se sabe hasta cuándo ocupa la sala una función.
    public LocalDateTime terminaSiEmpiezaA(LocalDateTime inicio) {
        return inicio.plusMinutes(duracion);
    }

    public boolean tieneFunciones() {
        return !funciones.isEmpty();
    }

    public boolean tieneEvaluaciones() {
        return !evaluaciones.isEmpty();
    }

    public boolean tieneVotos() {
        return !votosPublico.isEmpty();
    }

    // CORRECCION I14: al eliminarla se la quita también de su director y de sus actores.
    public void desvincular() {
        if (tieneFunciones()) {
            throw new ReglaNegocioException("No se puede eliminar " + titulo + ": tiene funciones programadas.");
        }
        if (tieneEvaluaciones() || tieneVotos()) {
            throw new ReglaNegocioException("No se puede eliminar " + titulo + ": tiene evaluaciones o votos registrados.");
        }
        director.quitarPelicula(this);
        for (Actor a : actores) {
            a.quitarPelicula(this);
        }
        actores.clear();
    }

    public double calcularPromedioPublico() {
        if (votosPublico.isEmpty()) return 0;
        double suma = 0;
        for (VotoPublico v : votosPublico) {
            suma += v.getPuntaje();
        }
        return suma / votosPublico.size();
    }

    public double calcularPromedio() {
        if (evaluaciones.isEmpty()) return 0;
        double suma = 0;
        for (Evaluacion e : evaluaciones) {
            suma += e.getPuntaje();
        }
        return suma / evaluaciones.size();
    }

    public boolean seTitula(String titulo) {
        return titulo != null && this.titulo.equalsIgnoreCase(titulo.trim());
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
    // CORRECCION I13: las listas no se pueden modificar desde afuera.
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

    // CORRECCION S15: el título identifica a la película.
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Pelicula)) return false;
        return titulo.equalsIgnoreCase(((Pelicula) otro).titulo);
    }

    @Override
    public int hashCode() {
        return titulo.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return titulo + " (" + genero + ", " + duracion + " min) - Dir: " + director.getNombre();
    }
}
