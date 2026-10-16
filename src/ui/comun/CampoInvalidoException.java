package ui.comun;

import javax.swing.JComponent;

// Error de formato en un campo del formulario: se muestra el mensaje y se vuelve el foco al campo
public class CampoInvalidoException extends Exception {
    private final transient JComponent campo;

    public CampoInvalidoException(JComponent campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public JComponent getCampo() {
        return campo;
    }
}
