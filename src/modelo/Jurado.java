package modelo;

import java.util.ArrayList;
import java.util.List;

public class Jurado extends Persona {
    private List<Evaluacion> evaluaciones;

    public Jurado(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.evaluaciones = new ArrayList<>();
    }

    public void agregarEvaluacion(Evaluacion e) {
        evaluaciones.add(e);
    }

    public List<Evaluacion> getEvaluaciones() {
        return evaluaciones;
    }

    @Override
    public String toString() {
        return "Jurado: " + getNombre();
    }
}