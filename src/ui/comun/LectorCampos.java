package ui.comun;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

// CORRECCIÓN S8: lectura y validación de formato de los campos de texto en un solo lugar.
// Antes las mismas validaciones de fecha, horario y números estaban copiadas en cada diálogo.
// CORRECCIÓN S7: acá solo se controla el formato; las reglas del negocio las valida el modelo.
public final class LectorCampos {

    private LectorCampos() {
    }

    public static String texto(JTextField campo, String nombre) throws CampoInvalidoException {
        String valor = campo.getText().trim();
        if (valor.isEmpty()) {
            throw new CampoInvalidoException("Complete el campo " + nombre + ".", campo);
        }
        return valor;
    }

    public static int entero(JTextField campo, String nombre) throws CampoInvalidoException {
        String valor = texto(campo, nombre);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new CampoInvalidoException(nombre + ": debe ser un número entero.", campo);
        }
    }

    // Acepta coma o punto como separador decimal (por ejemplo 8,5 o 8.5)
    public static double decimal(JTextField campo, String nombre) throws CampoInvalidoException {
        String valor = texto(campo, nombre);
        try {
            return Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new CampoInvalidoException(nombre + ": debe ser un número. Ej: 8.5", campo);
        }
    }

    public static LocalDate fecha(JTextField campo, String nombre) throws CampoInvalidoException {
        String valor = texto(campo, nombre);
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            throw new CampoInvalidoException(nombre + ": formato incorrecto. Use AAAA-MM-DD.", campo);
        }
    }

    public static LocalTime hora(JTextField campo, String nombre) throws CampoInvalidoException {
        String valor = texto(campo, nombre);
        try {
            return LocalTime.parse(valor);
        } catch (DateTimeParseException e) {
            throw new CampoInvalidoException(nombre + ": formato incorrecto. Use HH:MM.", campo);
        }
    }
}
