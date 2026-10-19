package modelo.festival;

import excepciones.EntidadDuplicadaException;
import modelo.comun.Validar;
import modelo.peliculas.Pelicula;

import java.util.ArrayList;
import java.util.Collections;
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
        Validar.presente(pelicula, "La película es obligatoria.");
        if (contiene(pelicula)) {
            throw new EntidadDuplicadaException(pelicula.getTitulo() + " ya está en la sección " + tipo + ".");
        }
        peliculas.add(pelicula);
    }

    // Devuelve true si la película estaba en la sección. Lo usa Edicion, que controla las funciones
    boolean quitarPelicula(Pelicula pelicula) {
        return peliculas.remove(pelicula);
    }

    public boolean contiene(Pelicula pelicula) {
        return peliculas.contains(pelicula);
    }

    // Ganadora: la película con mejor promedio del jurado, entre las que tienen evaluaciones
    public Optional<Pelicula> determinarGanadora() {
        return Premiacion.mejorPromedio(peliculas, Pelicula::tieneEvaluaciones, Pelicula::calcularPromedioJurado);
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
