package modelo.peliculas;

import excepciones.VotoInvalidoException;
import modelo.comun.Validar;
import modelo.personas.Espectador;

public class VotoPublico {
    public static final int PUNTAJE_MINIMO = 1;
    public static final int PUNTAJE_MAXIMO = 5;

    private final int puntaje;
    private final Pelicula pelicula;
    private final Espectador espectador;

    // Lo crea Pelicula.recibirVoto (Creator); la validación del puntaje vive acá (Experto)
    VotoPublico(int puntaje, Pelicula pelicula, Espectador espectador) {
        if (puntaje < PUNTAJE_MINIMO || puntaje > PUNTAJE_MAXIMO) {
            throw new VotoInvalidoException("el puntaje debe estar entre 1 y 5.");
        }
        this.puntaje = puntaje;
        this.pelicula = Validar.presente(pelicula, "La película es obligatoria.");
        this.espectador = Validar.presente(espectador, "El espectador es obligatorio.");
    }

    public boolean esDe(Espectador otro) {
        return espectador.equals(otro);
    }

    public int getPuntaje() { return puntaje; }
    public Pelicula getPelicula() { return pelicula; }
    public Espectador getEspectador() { return espectador; }

    @Override
    public String toString() {
        return "Voto de " + espectador.getNombre() + " a " + pelicula.getTitulo() + " - Puntaje: " + puntaje;
    }
}
