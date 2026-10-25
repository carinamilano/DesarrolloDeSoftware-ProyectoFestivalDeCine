package modelo;

import excepciones.DatoInvalidoException;

public class Evaluacion {
    public static final double PUNTAJE_MINIMO = 0;
    public static final double PUNTAJE_MAXIMO = 10;

    private double puntaje;
    private Pelicula pelicula;
    private Jurado jurado;

    // CORRECCION I14: la crea Pelicula.registrarEvaluacion, que la vincula con la película y el jurado.
    Evaluacion(double puntaje, Pelicula pelicula, Jurado jurado) {
        /* CORRECCION I9: el rango se valida acá; antes solo lo controlaba el servicio
         y las evaluaciones cargadas desde archivo no pasaban por ese control.*/
        if (puntaje < PUNTAJE_MINIMO || puntaje > PUNTAJE_MAXIMO) {
            throw new DatoInvalidoException("El puntaje debe estar entre 0 y 10.");
        }
        this.puntaje = puntaje;
        this.pelicula = pelicula;
        this.jurado = jurado;
    }

    public double getPuntaje() { return puntaje; }
    public Pelicula getPelicula() { return pelicula; }
    public Jurado getJurado() { return jurado; }

    @Override
    public String toString() {
        return "Evaluacion de " + pelicula.getTitulo() +
                " por " + jurado.getNombre() +
                " - Puntaje: " + puntaje;
    }
}
