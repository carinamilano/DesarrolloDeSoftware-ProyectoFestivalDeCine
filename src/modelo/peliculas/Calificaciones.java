package modelo.peliculas;

import modelo.personas.Espectador;
import modelo.personas.Jurado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

// Evaluaciones del jurado y votos del público de una película, con sus promedios.
// Se extrajo de Pelicula (Large Class): Pelicula valida las reglas y le delega guardar y promediar
final class Calificaciones {
    private final List<Evaluacion> evaluaciones = new ArrayList<>();
    private final List<VotoPublico> votos = new ArrayList<>();

    void agregar(Evaluacion evaluacion) {
        evaluaciones.add(evaluacion);
    }

    void agregar(VotoPublico voto) {
        votos.add(voto);
    }

    boolean fueEvaluadaPor(Jurado jurado) {
        return evaluaciones.stream().anyMatch(evaluacion -> evaluacion.esDe(jurado));
    }

    boolean fueVotadaPor(Espectador espectador) {
        return votos.stream().anyMatch(voto -> voto.esDe(espectador));
    }

    double promedioJurado() {
        return promedio(evaluaciones, Evaluacion::getPuntaje);
    }

    double promedioPublico() {
        return promedio(votos, VotoPublico::getPuntaje);
    }

    private static <T> double promedio(List<T> elementos, ToDoubleFunction<T> puntaje) {
        return elementos.stream().mapToDouble(puntaje).average().orElse(0);
    }

    boolean tieneEvaluaciones() {
        return !evaluaciones.isEmpty();
    }

    boolean tieneVotos() {
        return !votos.isEmpty();
    }

    int cantidadVotos() {
        return votos.size();
    }

    List<Evaluacion> getEvaluaciones() {
        return Collections.unmodifiableList(evaluaciones);
    }

    List<VotoPublico> getVotos() {
        return Collections.unmodifiableList(votos);
    }
}
