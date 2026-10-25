package ui.comun;

import javax.swing.*;

/* CORRECCION S6/S8: error de formato en un campo de un formulario (vacío, no es número, no es fecha...).
Trae el campo para poder darle el foco al mostrar el mensaje.*/
public class CampoInvalidoException extends Exception {
    private final JComponent campo;

    public CampoInvalidoException(String mensaje, JComponent campo) {
        super(mensaje);
        this.campo = campo;
    }

    public JComponent getCampo() {
        return campo;
    }
}
