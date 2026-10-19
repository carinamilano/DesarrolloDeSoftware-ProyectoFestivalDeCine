package modelo.personas;

import excepciones.ReglaNegocioException;
import modelo.peliculas.Evaluacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jurado extends Persona {
    private final List<Evaluacion> evaluaciones;

    public Jurado(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.evaluaciones = new ArrayList<>();
    }

    // Lo llama Pelicula.recibirEvaluacion. Es público porque Pelicula está en otro paquete;
    // por eso solo acepta evaluaciones hechas por este jurado
    public void registrarEvaluacion(Evaluacion evaluacion) {
        if (!evaluacion.esDe(this)) {
            throw new ReglaNegocioException("La evaluación pertenece a otro jurado.");
        }
        evaluaciones.add(evaluacion);
    }

    public List<Evaluacion> getEvaluaciones() {
        return Collections.unmodifiableList(evaluaciones);
    }

    @Override
    protected String getRol() {
        return "Jurado";
    }
}
