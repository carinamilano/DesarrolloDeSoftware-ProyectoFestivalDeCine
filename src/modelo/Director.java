package modelo;

import java.util.ArrayList;
import java.util.List;

public class Director extends Persona {
    private List<Pelicula> peliculasDirigidas;

    public Director(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.peliculasDirigidas = new ArrayList<>();
    }

    public void agregarPelicula(Pelicula p) {
        peliculasDirigidas.add(p);
    }

    public void quitarPelicula(Pelicula p) {
        peliculasDirigidas.remove(p);
    }

    public List<Pelicula> getPeliculasDirigidas() {
        return peliculasDirigidas; }

    @Override
    public String toString() {
        return "Director: " + getNombre();
    }
}