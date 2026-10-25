package persistencia;

import excepciones.DatoInvalidoException;

import java.util.ArrayList;
import java.util.List;

/* CORRECCIÓN P7: los campos se separan con ';' y, si un texto contiene ';' o comillas, se escribe
entre comillas (las comillas internas se duplican). Antes se usaba split(",") sin escape
y una coma dentro de un título corría todas las columnas.*/
final class FormatoLinea {
    static final char SEPARADOR = ';';
    private static final char COMILLA = '"';

    private FormatoLinea() {
    }

    static String unir(Object... campos) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) linea.append(SEPARADOR);
            linea.append(escapar(campos[i] == null ? "" : campos[i].toString()));
        }
        return linea.toString();
    }

    private static String escapar(String campo) {
        if (campo.indexOf(SEPARADOR) >= 0 || campo.indexOf(COMILLA) >= 0) {
            return COMILLA + campo.replace("\"", "\"\"") + COMILLA;
        }
        return campo;
    }

    static List<String> separar(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (entreComillas) {
                if (c == COMILLA && i + 1 < linea.length() && linea.charAt(i + 1) == COMILLA) {
                    actual.append(COMILLA);
                    i++;
                } else if (c == COMILLA) {
                    entreComillas = false;
                } else {
                    actual.append(c);
                }
            } else if (c == COMILLA) {
                entreComillas = true;
            } else if (c == SEPARADOR) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (entreComillas) {
            throw new DatoInvalidoException("hay comillas sin cerrar.");
        }
        campos.add(actual.toString());
        return campos;
    }
}
