package repositorio;

import excepciones.EntidadNoEncontradaException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// Abstracción de la que dependen los servicios (DIP): si mañana se guarda en una base de datos,
// se escribe otra implementación y los servicios no cambian
public interface Repositorio<T> {

    void agregar(T elemento);

    boolean quitar(T elemento);

    // Vista de solo lectura: para agregar o quitar hay que pasar por el repositorio
    List<T> listar();

    Optional<T> buscar(Predicate<T> criterio);

    default boolean existe(Predicate<T> criterio) {
        return buscar(criterio).isPresent();
    }

    default boolean estaVacio() {
        return listar().isEmpty();
    }

    // Reemplaza las 12 búsquedas lineales copiadas (for + equalsIgnoreCase + throw) del código original
    default T obtener(Predicate<T> criterio, String entidad, String detalle) {
        return buscar(criterio).orElseThrow(() -> new EntidadNoEncontradaException(entidad, detalle));
    }
}
