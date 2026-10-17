package ui.comun;

import javax.swing.JTextField;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

// La UI solo controla el formato de lo que se escribe (vacío, número, fecha).
// Las reglas de negocio (rangos, fechas válidas, duplicados) las valida el modelo
public final class LectorCampos {

    private LectorCampos() {
    }

    public static String texto(JTextField campo, String siEstaVacio) throws CampoInvalidoException {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new CampoInvalidoException(campo, siEstaVacio);
        }
        return texto;
    }

    public static int entero(JTextField campo, String siEstaVacio, String siNoEsNumero) throws CampoInvalidoException {
        String texto = texto(campo, siEstaVacio);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new CampoInvalidoException(campo, siNoEsNumero);
        }
    }

    public static double decimal(JTextField campo, String siEstaVacio, String siNoEsNumero) throws CampoInvalidoException {
        String texto = texto(campo, siEstaVacio);
        try {
            return Double.parseDouble(texto.replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new CampoInvalidoException(campo, siNoEsNumero);
        }
    }

    public static LocalDate fecha(JTextField campo, String siEstaVacio) throws CampoInvalidoException {
        String texto = texto(campo, siEstaVacio);
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException ex) {
            throw new CampoInvalidoException(campo, "Formato de fecha incorrecto. Use AAAA-MM-DD.");
        }
    }

    public static LocalTime horario(JTextField campo, String siEstaVacio) throws CampoInvalidoException {
        String texto = texto(campo, siEstaVacio);
        try {
            return LocalTime.parse(texto);
        } catch (DateTimeParseException ex) {
            throw new CampoInvalidoException(campo, "Formato de horario incorrecto. Use HH:MM.");
        }
    }
}
