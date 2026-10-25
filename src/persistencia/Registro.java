package persistencia;

import excepciones.DatoInvalidoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/* CORRECCION P2/P12: una línea leída de un archivo. Cada metodo convierte un campo y, si no puede,
lanza una DatoInvalidoException que explica qué campo falló, en lugar de un
NumberFormatException o ArrayIndexOutOfBoundsException que antes cerraba la aplicación.*/
class Registro {
    private final String archivo;
    private final int numeroLinea;
    private final List<String> campos;

    Registro(String archivo, int numeroLinea, String linea) {
        this.archivo = archivo;
        this.numeroLinea = numeroLinea;
        this.campos = FormatoLinea.separar(linea);
    }

    String ubicacion() {
        return archivo + ", línea " + numeroLinea;
    }

    String texto(int i) {
        if (i >= campos.size() || campos.get(i).isBlank()) {
            throw new DatoInvalidoException("falta el campo " + (i + 1) + ".");
        }
        return campos.get(i).trim();
    }

    String textoOpcional(int i) {
        return i < campos.size() ? campos.get(i).trim() : "";
    }

    int entero(int i) {
        String valor = texto(i);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("el campo " + (i + 1) + " ('" + valor + "') no es un número entero.");
        }
    }

    double decimal(int i) {
        String valor = texto(i);
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("el campo " + (i + 1) + " ('" + valor + "') no es un número.");
        }
    }

    LocalDate fecha(int i) {
        return convertirFecha(texto(i), i);
    }

    LocalTime hora(int i) {
        String valor = texto(i);
        try {
            return LocalTime.parse(valor);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException("el campo " + (i + 1) + " ('" + valor + "') no es una hora HH:MM.");
        }
    }

    List<LocalDate> fechas(int i) {
        List<LocalDate> fechas = new ArrayList<>();
        String valor = textoOpcional(i);
        if (valor.isEmpty()) return fechas;
        for (String parte : valor.split(",")) {
            fechas.add(convertirFecha(parte.trim(), i));
        }
        return fechas;
    }

    <E extends Enum<E>> E enumerado(int i, Class<E> tipo) {
        String valor = texto(i);
        try {
            return Enum.valueOf(tipo, valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DatoInvalidoException("el campo " + (i + 1) + " ('" + valor + "') no es un valor válido.");
        }
    }

    private LocalDate convertirFecha(String valor, int i) {
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException("el campo " + (i + 1) + " ('" + valor + "') no es una fecha AAAA-MM-DD.");
        }
    }
}
