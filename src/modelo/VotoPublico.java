package modelo;

public class VotoPublico {
    private int puntaje;
    private Pelicula pelicula;
    private Espectador espectador;

    public VotoPublico(int puntaje, Pelicula pelicula, Espectador espectador) {
        this.puntaje = puntaje;
        this.pelicula = pelicula;
        this.espectador = espectador;
    }

    public int getPuntaje() { return puntaje; }
    public Pelicula getPelicula() { return pelicula; }
    public Espectador getEspectador() { return espectador; }

    @Override
    public String toString() {
        return "Voto de " + espectador.getNombre() + " a " + pelicula.getTitulo() + " - Puntaje: " + puntaje;
    }
}
