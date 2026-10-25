package ui.comun;

import javax.swing.*;
import java.awt.*;

/* CORRECCION S8: un solo formulario de persona (nombre, email y nacionalidad).
Antes había cuatro casi iguales: en PanelDirectores, PanelActores, PanelEspectadores y PanelEvaluaciones.
Quien lo usa lo muestra, intenta registrar a la persona y, si el servicio rechaza los datos
(por ejemplo, email repetido), muestra el error y lo vuelve a abrir con lo que se había escrito.*/
public class DialogoPersona extends JDialog {
    private JTextField campoNombre;
    private JTextField campoEmail;
    private JTextField campoNacionalidad;
    private JLabel labelError;
    private boolean aceptado;

    public DialogoPersona(Window padre, String titulo) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        setSize(400, 250);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = ComponentesUI.crearPanelCampos(3);
        campoNombre = new JTextField();
        campoEmail = new JTextField();
        campoNacionalidad = new JTextField();
        panelCampos.add(new JLabel("Nombre:"));
        panelCampos.add(campoNombre);
        panelCampos.add(new JLabel("Email:"));
        panelCampos.add(campoEmail);
        panelCampos.add(new JLabel("Nacionalidad:"));
        panelCampos.add(campoNacionalidad);

        labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoNombre.addActionListener(e -> campoEmail.requestFocus());
        campoEmail.addActionListener(e -> campoNacionalidad.requestFocus());
        campoNacionalidad.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> setVisible(false));
        btnAceptar.addActionListener(e -> aceptar());

        add(panelCampos, BorderLayout.CENTER);
        add(ComponentesUI.crearPanelSur(labelError, btnAceptar, btnCancelar), BorderLayout.SOUTH);
    }


    public boolean mostrar() {
        aceptado = false;
        setVisible(true);
        return aceptado;
    }

    public void mostrarError(String mensaje) {
        labelError.setText(mensaje);
    }

    public String getNombre() { return campoNombre.getText().trim(); }
    public String getEmail() { return campoEmail.getText().trim(); }
    public String getNacionalidad() { return campoNacionalidad.getText().trim(); }

    private void aceptar() {
        try {
            LectorCampos.texto(campoNombre, "Nombre");
            LectorCampos.texto(campoEmail, "Email");
            LectorCampos.texto(campoNacionalidad, "Nacionalidad");
            aceptado = true;
            setVisible(false);
        } catch (CampoInvalidoException ex) {
            labelError.setText(ex.getMessage());
            ex.getCampo().requestFocus();
        }
    }
}
