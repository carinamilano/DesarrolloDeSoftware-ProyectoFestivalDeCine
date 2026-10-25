package ui.entradas;

import excepciones.FestivalException;
import modelo.*;
import servicios.AbonoServicio;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import ui.comun.ComponentesUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

// CORRECCION S8: el diálogo de compra de abonos salió de PanelAbonos a su propia clase.
public class DialogoComprarAbono extends JDialog {
    private Abono abonoComprado;

    public DialogoComprarAbono(Window padre, AbonoServicio abonoServicio, FuncionServicio funcionServicio, PersonaServicio personaServicio) {
        super(padre, "Comprar abono", ModalityType.APPLICATION_MODAL);
        setSize(520, 420);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        JComboBox<Espectador> comboEspectador = ComponentesUI.crearComboPersonas(personaServicio.getEspectadores());

        // CORRECCION S4: el combo usa el enum TipoAbono en lugar de dos arreglos de textos.
        JComboBox<TipoAbono> comboTipo = new JComboBox<>(TipoAbono.values());
        JComboBox<TipoTarifa> comboTarifa = new JComboBox<>(TipoTarifa.values());
        JLabel labelPrecio = new JLabel();
        labelPrecio.setFont(new Font("SansSerif", Font.BOLD, 13));
        JPanel panelCampos = ComponentesUI.crearPanelCampos(4);
        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(ComponentesUI.crearSelectorEspectador(this, personaServicio, comboEspectador));
        panelCampos.add(new JLabel("Tipo de abono:"));
        panelCampos.add(comboTipo);
        panelCampos.add(new JLabel("Tarifa:"));
        panelCampos.add(comboTarifa);
        panelCampos.add(new JLabel("Precio:"));
        panelCampos.add(labelPrecio);
        List<LocalDate> fechas = funcionServicio.getFechasConFuncionesPresenciales();
        JList<LocalDate> listaDias = new JList<>(fechas.toArray(new LocalDate[0]));
        listaDias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaDias.setVisibleRowCount(5);
        JPanel panelDias = new JPanel(new BorderLayout());
        panelDias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 20),
                BorderFactory.createTitledBorder("Elegí 3 días (Ctrl + clic)")));
        panelDias.add(new JScrollPane(listaDias), BorderLayout.CENTER);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelCampos, BorderLayout.NORTH);
        panelCentro.add(panelDias, BorderLayout.CENTER);

        Runnable actualizar = () -> {
            TipoAbono tipo = ComponentesUI.seleccionado(comboTipo);
            listaDias.setEnabled(tipo == TipoAbono.TRES_DIAS);
            double precio = abonoServicio.calcularPrecioAbono(tipo, ComponentesUI.seleccionado(comboTarifa));
            labelPrecio.setText(ComponentesUI.formatearPrecio(precio));
        };
        comboTipo.addActionListener(e -> actualizar.run());
        comboTarifa.addActionListener(e -> actualizar.run());
        actualizar.run();

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Comprar");
        JButton btnCancelar = new JButton("Cancelar");

        btnCancelar.addActionListener(e -> dispose());

        // CORRECCION S4/S10: el switch es sobre el enum, no sobre la posición del combo & que sean exactamente 3 días distintos lo valida AbonoTresDias.
        btnAceptar.addActionListener(e -> {
            Espectador espectador = ComponentesUI.seleccionado(comboEspectador);
            TipoTarifa tarifa = ComponentesUI.seleccionado(comboTarifa);
            try {
                switch (ComponentesUI.seleccionado(comboTipo)) {
                    case TRES_DIAS -> abonoComprado = abonoServicio.comprarAbonoTresDias(espectador, tarifa, listaDias.getSelectedValuesList());
                    case FULL -> abonoComprado = abonoServicio.comprarAbonoFull(espectador, tarifa);
                    case STREAMING -> abonoComprado = abonoServicio.comprarAbonoStreaming(espectador, tarifa);
                }
                dispose();
            } catch (FestivalException ex) {
                labelError.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                labelError.setText(ComponentesUI.errorInesperado(ex));
            }
        });

        add(panelCentro, BorderLayout.CENTER);
        add(ComponentesUI.crearPanelSur(labelError, btnAceptar, btnCancelar), BorderLayout.SOUTH);
    }

    public boolean mostrar() {
        setVisible(true);
        return abonoComprado != null;
    }

    public Abono getAbonoComprado() {
        return abonoComprado;
    }
}
