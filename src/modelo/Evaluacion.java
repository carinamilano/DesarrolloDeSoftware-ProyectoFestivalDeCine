package modelo;

public class Evaluacion {
    private double puntaje;
    private Pelicula pelicula;
    private Jurado jurado;

    public Evaluacion(double puntaje, Pelicula pelicula, Jurado jurado) {
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
