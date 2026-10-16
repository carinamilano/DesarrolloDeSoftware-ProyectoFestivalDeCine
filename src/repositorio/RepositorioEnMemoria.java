package repositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RepositorioEnMemoria<T> implements Repositorio<T> {
    private final List<T> elementos = new ArrayList<>();

    @Override
    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    @Override
    public boolean quitar(T elemento) {
        return elementos.remove(elemento);
    }

    @Override
    public List<T> listar() {
        return Collections.unmodifiableList(elementos);
    }

    @Override
    public Optional<T> buscar(Predicate<T> criterio) {
        return elementos.stream().filter(criterio).findFirst();
    }
}
