package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* CORRECCION S16: Pelicula tenía baja cohesión: además de sus datos, guardaba las evaluaciones y los
votos y calculaba los promedios. Esa parte se extrajo a esta clase (Extract Class). Cada película tiene
sus calificaciones; las reglas que necesitan conocer a la película (un jurado evalúa una sola vez, para
votar hay que tener entrada) siguen en Pelicula. */
public class Calificaciones {

    private final List<Evaluacion> evaluaciones;
    private final List<VotoPublico> votosPublico;

    Calificaciones() {
        this.evaluaciones = new ArrayList<>();
        this.votosPublico = new ArrayList<>();
    }

    // Solo los llama Pelicula, después de validar sus reglas.
    void agregar(Evaluacion evaluacion) {
        evaluaciones.add(evaluacion);
    }

    void agregar(VotoPublico voto) {
        votosPublico.add(voto);
    }

    public boolean fueEvaluadaPor(Jurado jurado) {
        for (Evaluacion e : evaluaciones) {
            if (e.getJurado().equals(jurado)) return true;
        }
        return false;
    }

    public boolean fueVotadaPor(Espectador espectador) {
        for (VotoPublico v : votosPublico) {
            // CORRECCION S13: compara con equals (por email) en lugar de ==.
            if (v.getEspectador().equals(espectador)) return true;
        }
        return false;
    }

    public boolean tieneEvaluaciones() {
        return !evaluaciones.isEmpty();
    }

    public boolean tieneVotos() {
        return !votosPublico.isEmpty();
    }

    public int cantidadEvaluaciones() {
        return evaluaciones.size();
    }

    public int cantidadVotos() {
        return votosPublico.size();
    }

    public double calcularPromedio() {
        if (evaluaciones.isEmpty()) return 0;
        double suma = 0;
        for (Evaluacion e : evaluaciones) {
            suma += e.getPuntaje();
        }
        return suma / evaluaciones.size();
    }

    public double calcularPromedioPublico() {
        if (votosPublico.isEmpty()) return 0;
        double suma = 0;
        for (VotoPublico v : votosPublico) {
            suma += v.getPuntaje();
        }
        return suma / votosPublico.size();
    }

    // CORRECCION I13: las listas no se pueden modificar desde afuera.
    public List<Evaluacion> getEvaluaciones() {
        return Collections.unmodifiableList(evaluaciones);
    }

    public List<VotoPublico> getVotosPublico() {
        return Collections.unmodifiableList(votosPublico);
    }
}
