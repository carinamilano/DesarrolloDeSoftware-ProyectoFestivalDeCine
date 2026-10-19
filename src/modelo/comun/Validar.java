package modelo.comun;

import excepciones.DatoInvalidoException;

// Validaciones de datos comunes a todo el modelo (antes estaban repetidas en la UI y en los servicios)
public final class Validar {

    private Validar() {
    }

    public static String texto(String valor, String mensajeSiFalta) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException(mensajeSiFalta);
        }
        return valor.trim();
    }

    public static <T> T presente(T valor, String mensajeSiFalta) {
        if (valor == null) {
            throw new DatoInvalidoException(mensajeSiFalta);
        }
        return valor;
    }
}
