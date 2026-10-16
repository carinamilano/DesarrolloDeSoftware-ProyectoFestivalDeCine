package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jurado extends Persona {
    private final List<Evaluacion> evaluaciones;

    public Jurado(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.evaluaciones = new ArrayList<>();
    }

    void registrarEvaluacion(Evaluacion evaluacion) {
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
