package modelo.festival;

import modelo.peliculas.Pelicula;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

// Criterio común de las dos premiaciones (jurado por sección y público por edición): gana la película
// con mejor promedio entre las que fueron calificadas. Antes estaba copiado en Seccion y en Edicion
final class Premiacion {

    private Premiacion() {
    }

    static Optional<Pelicula> mejorPromedio(List<Pelicula> candidatas, Predicate<Pelicula> fueCalificada,
                                            ToDoubleFunction<Pelicula> promedio) {
        return candidatas.stream()
                .filter(fueCalificada)
                .max(Comparator.comparingDouble(promedio));
    }
}
