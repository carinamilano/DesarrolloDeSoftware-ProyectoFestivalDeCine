package ui.entradas;

import excepciones.FestivalException;
import modelo.*;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import servicios.VentaServicio;
import ui.comun.ComponentesUI;

import javax.swing.*;
import java.awt.*;

// CORRECCION S8: el diálogo de venta streaming salió de PanelVentaEntradas a su propia clase.
public class DialogoVentaStreaming extends JDialog {
    private Entrada entradaVendida;

    public DialogoVentaStreaming(Window padre, VentaServicio ventaServicio, FuncionServicio funcionServicio,
                                 PersonaServicio personaServicio) {
        super(padre, "Vender entrada streaming", ModalityType.APPLICATION_MODAL);
        setSize(560, 260);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // CORRECCION S7/I1: solo se ofrecen las funciones que todavía no comenzaron.
        // CORRECCION S10/I10: los combos guardan los objetos, el espectador se muestra con su email.
        JComboBox<Espectador> comboEspectador = ComponentesUI.crearComboPersonas(personaServicio.getEspectadores());
        JComboBox<FuncionStreaming> comboFuncion = ComponentesUI.crearComboFunciones(funcionServicio.getFuncionesStreamingEnVenta());
        JComboBox<TipoTarifa> comboTarifa = new JComboBox<>(TipoTarifa.values());

        JPanel panelCampos = ComponentesUI.crearPanelCampos(3);
        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(ComponentesUI.crearSelectorEspectador(this, personaServicio, comboEspectador));
        panelCampos.add(new JLabel("Función:"));
        panelCampos.add(comboFuncion);
        panelCampos.add(new JLabel("Tarifa:"));
        panelCampos.add(comboTarifa);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        btnCancelar.addActionListener(e -> dispose());

        btnAceptar.addActionListener(e -> {
            try {
                entradaVendida = ventaServicio.venderEntradaStreaming(ComponentesUI.seleccionado(comboEspectador),
                        ComponentesUI.seleccionado(comboFuncion), ComponentesUI.seleccionado(comboTarifa));
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
        return entradaVendida != null;
    }

    public Entrada getEntradaVendida() {
        return entradaVendida;
    }
}
