package persistencia;

import modelo.precios.Dinero;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Una línea de un archivo, con acceso a sus campos por posición y las conversiones que usa la carga.
// Los campos se separan recién cuando se piden: si la línea está mal formada, el error queda en esa línea
final class Registro {
    static final String SEPARADOR_LISTA = "|";

    private final String entidad;
    private final int numeroLinea;
    private final String linea;
    private List<String> campos;

    Registro(String entidad, int numeroLinea, String linea) {
        this.entidad = entidad;
        this.numeroLinea = numeroLinea;
        this.linea = linea;
    }

    private List<String> campos() {
        if (campos == null) {
            campos = FormatoCsv.separar(linea);
        }
        return campos;
    }

    String texto(int posicion) {
        if (posicion >= campos().size()) {
            throw new IllegalArgumentException("falta el campo " + (posicion + 1) + ".");
        }
        return campos().get(posicion).trim();
    }

    // Un campo vacío o que no está
    Optional<String> opcional(int posicion) {
        if (posicion >= campos().size() || campos().get(posicion).isBlank()) {
            return Optional.empty();
        }
        return Optional.of(campos().get(posicion).trim());
    }

    int entero(int posicion) {
        return Integer.parseInt(texto(posicion));
    }

    double decimal(int posicion) {
        return Double.parseDouble(texto(posicion));
    }

    LocalDate fecha(int posicion) {
        return LocalDate.parse(texto(posicion));
    }

    LocalDateTime fechaHora(int posicion) {
        return LocalDateTime.parse(texto(posicion));
    }

    Dinero dinero(int posicion) {
        return Dinero.de(texto(posicion));
    }

    List<LocalDate> fechas(int posicion) {
        return Arrays.stream(texto(posicion).split("\\" + SEPARADOR_LISTA)).map(String::trim).map(LocalDate::parse).toList();
    }

    <E extends Enum<E>> E enumerado(int posicion, Class<E> tipo) {
        return Enum.valueOf(tipo, texto(posicion).toUpperCase(Locale.ROOT));
    }

    String describir() {
        return entidad + " (línea " + numeroLinea + "): " + linea;
    }
}
