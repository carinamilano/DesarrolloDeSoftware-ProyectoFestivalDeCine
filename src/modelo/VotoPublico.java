package modelo;

import excepciones.VotoInvalidoException;

public class VotoPublico {
    public static final int PUNTAJE_MINIMO = 1;
    public static final int PUNTAJE_MAXIMO = 5;

    private int puntaje;
    private Pelicula pelicula;
    private Espectador espectador;

    // CORRECCION I14: lo crea Pelicula.registrarVoto, después de validar que el espectador puede votar.
    VotoPublico(int puntaje, Pelicula pelicula, Espectador espectador) {
        // CORRECCION I9: el rango se valida acá, no solo en el servicio.
        if (puntaje < PUNTAJE_MINIMO || puntaje > PUNTAJE_MAXIMO) {
            throw new VotoInvalidoException("el puntaje debe estar entre 1 y 5.");
        }
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
