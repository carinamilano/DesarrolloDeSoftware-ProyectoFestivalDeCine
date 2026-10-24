package modelo;

import java.util.ArrayList;
import java.util.List;

public class Seccion {
    private TipoSeccion tipo;
    private Premio premio;
    private List<Pelicula> peliculas;

    public Seccion(TipoSeccion tipo, Premio premio) {
        this.tipo = tipo;
        this.premio = premio;
        this.peliculas = new ArrayList<>();
    }

    public void agregarPelicula(Pelicula p) {
        peliculas.add(p);
    }

    public void eliminarPelicula(Pelicula p) {
        peliculas.remove(p);
    }

    public Pelicula determinarGanadora() {
        if (peliculas.isEmpty()) return null;
        Pelicula ganadora = peliculas.get(0);
        for (Pelicula p : peliculas) {
            if (p.calcularPromedio() > ganadora.calcularPromedio()) {
                ganadora = p;
            }
        }
        return ganadora;
    }

    public TipoSeccion getTipo() {
        return tipo;
    }
    public Premio getPremio() {
        return premio;
    }
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    @Override
    public String toString() {
        return "Seccion: " + tipo;
    }
}