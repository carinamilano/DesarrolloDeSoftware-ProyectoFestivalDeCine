package ui.festival;

import excepciones.FestivalException;
import modelo.Edicion;
import modelo.TipoSeccion;
import servicios.FestivalServicio;
import ui.comun.ComponentesUI;

import javax.swing.*;
import java.awt.*;

// CORRECCION S8: el diálogo de alta de sección salió de PanelSecciones a su propia clase.
public class DialogoAgregarSeccion extends JDialog {
    private TipoSeccion tipoAgregado;

    public DialogoAgregarSeccion(Window padre, FestivalServicio festivalServicio, Edicion edicion) {
        super(padre, "Agregar sección - Edición " + edicion.getAnio(), ModalityType.APPLICATION_MODAL);
        setSize(440, 250);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // Solo se ofrecen los tipos que la edición todavía no tiene
        JComboBox<TipoSeccion> comboTipo = new JComboBox<>(edicion.getTiposSinSeccion().toArray(new TipoSeccion[0]));
        JTextField campoPremio = new JTextField();
        JTextField campoDescripcion = new JTextField();

        JPanel panelCampos = ComponentesUI.crearPanelCampos(3);
        panelCampos.add(new JLabel("Tipo de sección:"));
        panelCampos.add(comboTipo);
        panelCampos.add(new JLabel("Nombre del premio:"));
        panelCampos.add(campoPremio);
        panelCampos.add(new JLabel("Descripción del premio:"));
        panelCampos.add(campoDescripcion);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoPremio.addActionListener(e -> campoDescripcion.requestFocus());
        campoDescripcion.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dispose());

        btnAceptar.addActionListener(e -> {
            TipoSeccion tipo = ComponentesUI.seleccionado(comboTipo);
            try {
                festivalServicio.registrarSeccion(edicion, tipo, campoPremio.getText(), campoDescripcion.getText());
                tipoAgregado = tipo;
                dispose();
            } catch (FestivalException ex) {
                labelError.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                labelError.setText(ComponentesUI.errorInesperado(ex));
            }
        });

        add(panelCampos, BorderLayout.CENTER);
        add(ComponentesUI.crearPanelSur(labelError, btnAceptar, btnCancelar), BorderLayout.SOUTH);
    }

    public boolean mostrar() {
        setVisible(true);
        return tipoAgregado != null;
    }

    public TipoSeccion getTipoAgregado() {
        return tipoAgregado;
    }
}
