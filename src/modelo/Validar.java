package modelo;

import excepciones.DatoInvalidoException;

/* CORRECCION I9: validaciones comunes que usan los constructores del modelo,
 así ningún objeto se crea con datos inválidos aunque no pase por la interfaz.*/
class Validar {

    private Validar() {
    }

    static String texto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException(mensaje);
        }
        return valor.trim();
    }

    static <T> T presente(T valor, String mensaje) {
        if (valor == null) {
            throw new DatoInvalidoException(mensaje);
        }
        return valor;
    }
}
