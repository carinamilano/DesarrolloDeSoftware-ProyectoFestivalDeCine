package ui.comun;

import javax.swing.*;
import java.awt.*;

// Diálogo modal con campos, un mensaje de error y los botones Aceptar / Cancelar.
// Antes cada panel armaba esto a mano (unas 25 líneas iguales por diálogo)
public class FormularioDialogo {

    @FunctionalInterface
    public interface Accion {
        void ejecutar() throws Exception;
    }

    private final JDialog dialogo;
    private final JPanel panelCampos;
    private final JPanel panelCentro;
    private final JLabel labelError;
    private final JButton botonAceptar;

    public FormularioDialogo(Window padre, String titulo, int ancho, int alto) {
        dialogo = new JDialog(padre, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setSize(ancho, alto);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setLayout(new BorderLayout(10, 10));

        panelCampos = new JPanel(new GridLayout(0, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));
        panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelCampos, BorderLayout.CENTER);

        labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(ComponentesUI.FUENTE_MENSAJE);
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        botonAceptar = new JButton("Aceptar");
        JButton botonCancelar = ComponentesUI.crearBoton("Cancelar", this::cerrar);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(botonAceptar);
        panelBotones.add(botonCancelar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        dialogo.add(panelCentro, BorderLayout.CENTER);
        dialogo.add(panelSur, BorderLayout.SOUTH);
    }

    public <C extends JComponent> C agregarCampo(String etiqueta, C campo) {
        panelCampos.add(new JLabel(etiqueta));
        panelCampos.add(campo);
        return campo;
    }

    public void agregarDebajo(JComponent componente) {
        panelCentro.add(componente, BorderLayout.SOUTH);
    }

    public void setTextoAceptar(String texto) {
        botonAceptar.setText(texto);
    }

    // Enter pasa al campo siguiente; en el último, acepta
    public void encadenarEnter(JTextField... campos) {
        for (int i = 0; i < campos.length; i++) {
            if (i + 1 < campos.length) {
                JTextField siguiente = campos[i + 1];
                campos[i].addActionListener(e -> siguiente.requestFocus());
            } else {
                campos[i].addActionListener(e -> botonAceptar.doClick());
            }
        }
    }

    public void alAceptar(Accion accion) {
        botonAceptar.addActionListener(e -> ejecutar(accion));
    }

    private void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (CampoInvalidoException ex) {
            mostrarError(ex.getMessage());
            ex.getCampo().requestFocus();
        } catch (Exception ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }

    public void mostrarError(String mensaje) {
        labelError.setText(mensaje);
    }

    public void limpiarError() {
        labelError.setText(" ");
    }

    public JDialog getDialogo() {
        return dialogo;
    }

    public void mostrar() {
        dialogo.setVisible(true);
    }

    public void cerrar() {
        dialogo.dispose();
    }
}
