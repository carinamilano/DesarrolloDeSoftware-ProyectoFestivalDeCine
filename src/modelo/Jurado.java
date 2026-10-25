package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jurado extends Persona {
    private List<Evaluacion> evaluaciones;

    public Jurado(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.evaluaciones = new ArrayList<>();
    }

    // CORRECCION I14: solo lo llama Pelicula.registrarEvaluacion, que actualiza los dos lados.
    void agregarEvaluacion(Evaluacion e) {
        evaluaciones.add(e);
    }

    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Evaluacion> getEvaluaciones() {
        return Collections.unmodifiableList(evaluaciones);
    }

    @Override
    public String toString() {
        return "Jurado: " + getNombre();
    }
}
