package modelo;

import excepciones.FestivalException;

public class Evaluacion {
    public static final double PUNTAJE_MINIMO = 0;
    public static final double PUNTAJE_MAXIMO = 10;

    private final double puntaje;
    private final Pelicula pelicula;
    private final Jurado jurado;

    // La crea Pelicula.recibirEvaluacion (Creator); la validación del puntaje vive acá (Experto)
    Evaluacion(double puntaje, Pelicula pelicula, Jurado jurado) {
        if (puntaje < PUNTAJE_MINIMO || puntaje > PUNTAJE_MAXIMO) {
            throw new FestivalException("El puntaje debe estar entre 0 y 10.");
        }
        this.puntaje = puntaje;
        this.pelicula = Validar.presente(pelicula, "La película es obligatoria.");
        this.jurado = Validar.presente(jurado, "El jurado es obligatorio.");
    }

    public double getPuntaje() { return puntaje; }
    public Pelicula getPelicula() { return pelicula; }
    public Jurado getJurado() { return jurado; }
    public String getTituloPelicula() { return pelicula.getTitulo(); }
    public String getNombreJurado() { return jurado.getNombre(); }

    @Override
    public String toString() {
        return "Evaluacion de " + getTituloPelicula() + " por " + getNombreJurado() + " - Puntaje: " + puntaje;
    }
}
