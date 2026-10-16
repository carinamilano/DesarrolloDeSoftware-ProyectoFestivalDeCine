package modelo;

import excepciones.FestivalException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Seccion {
    private final TipoSeccion tipo;
    private final Premio premio;
    private final List<Pelicula> peliculas;

    Seccion(TipoSeccion tipo, Premio premio) {
        this.tipo = Validar.presente(tipo, "El tipo de sección es obligatorio.");
        this.premio = Validar.presente(premio, "Cada sección requiere un premio.");
        this.peliculas = new ArrayList<>();
    }

    public void agregarPelicula(Pelicula pelicula) {
        if (contiene(pelicula)) {
            throw new FestivalException(pelicula.getTitulo() + " ya está en la sección " + tipo + ".");
        }
        peliculas.add(pelicula);
    }

    // Devuelve true si la película estaba en la sección
    public boolean quitarPelicula(Pelicula pelicula) {
        return peliculas.remove(pelicula);
    }

    public boolean contiene(Pelicula pelicula) {
        return peliculas.contains(pelicula);
    }

    // Ganadora: la película con mejor promedio del jurado, entre las que tienen evaluaciones
    // (mismo criterio que el Premio del Público en Edicion)
    public Optional<Pelicula> determinarGanadora() {
        return peliculas.stream()
                .filter(Pelicula::tieneEvaluaciones)
                .max(Comparator.comparingDouble(Pelicula::calcularPromedioJurado));
    }

    public int cantidadPeliculas() {
        return peliculas.size();
    }

    public TipoSeccion getTipo() {
        return tipo;
    }
    public Premio getPremio() {
        return premio;
    }
    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }

    @Override
    public String toString() {
        return "Seccion: " + tipo;
    }
}
