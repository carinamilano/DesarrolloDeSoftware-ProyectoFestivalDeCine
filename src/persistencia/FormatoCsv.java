package persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Formato CSV (RFC 4180): un campo que tiene comas o comillas se escribe entre comillas y las
// comillas internas se duplican. Antes se usaba split(",") y un título con coma rompía la línea
final class FormatoCsv {
    private static final char SEPARADOR = ',';
    private static final char COMILLA = '"';

    private FormatoCsv() {
    }

    static String unir(List<String> campos) {
        return campos.stream().map(FormatoCsv::escapar).collect(Collectors.joining(String.valueOf(SEPARADOR)));
    }

    // Cada registro ocupa una línea: si un campo tuviera saltos de línea se reemplazan por espacios
    private static String escapar(String campo) {
        String limpio = campo.replace('\r', ' ').replace('\n', ' ');
        if (limpio.indexOf(SEPARADOR) < 0 && limpio.indexOf(COMILLA) < 0) {
            return limpio;
        }
        return COMILLA + limpio.replace("\"", "\"\"") + COMILLA;
    }

    static List<String> separar(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char caracter = linea.charAt(i);
            if (entreComillas) {
                if (caracter != COMILLA) {
                    campo.append(caracter);
                } else if (i + 1 < linea.length() && linea.charAt(i + 1) == COMILLA) {
                    campo.append(COMILLA); // comilla duplicada = una comilla dentro del campo
                    i++;
                } else {
                    entreComillas = false;
                }
            } else if (caracter == COMILLA) {
                entreComillas = true;
            } else if (caracter == SEPARADOR) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }
        if (entreComillas) {
            throw new IllegalArgumentException("hay comillas sin cerrar.");
        }
        campos.add(campo.toString());
        return campos;
    }
}
