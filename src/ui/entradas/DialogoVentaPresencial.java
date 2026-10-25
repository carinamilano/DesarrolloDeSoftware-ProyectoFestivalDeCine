package ui.entradas;

import excepciones.FestivalException;
import modelo.*;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import servicios.VentaServicio;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.LectorCampos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// CORRECCION S8: el diálogo de venta presencial salió de PanelVentaEntradas a su propia clase.
public class DialogoVentaPresencial extends JDialog {
    private Entrada ultimaEntrada;

    public DialogoVentaPresencial(Window padre, VentaServicio ventaServicio, FuncionServicio funcionServicio,
                                  PersonaServicio personaServicio) {
        super(padre, "Vender entrada presencial", ModalityType.APPLICATION_MODAL);
        setSize(560, 360);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // CORRECCION S7/I1: solo se ofrecen las funciones que todavía no comenzaron.
        // CORRECCION S10/I10: los combos guardan los objetos; el espectador se muestra con su email.
        JComboBox<Espectador> comboEspectador = ComponentesUI.crearComboPersonas(personaServicio.getEspectadores());
        JComboBox<FuncionPresencial> comboFuncion = ComponentesUI.crearComboFunciones(funcionServicio.getFuncionesPresencialesEnVenta());
        JTextField campoButaca = new JTextField();
        JComboBox<TipoTarifa> comboTarifa = new JComboBox<>(TipoTarifa.values());

        JLabel labelOcupadas = new JLabel("ninguna");
        labelOcupadas.setForeground(new Color(150, 50, 50));
        labelOcupadas.setFont(new Font("SansSerif", Font.ITALIC, 12));

        JPanel panelCampos = ComponentesUI.crearPanelCampos(5);
        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(ComponentesUI.crearSelectorEspectador(this, personaServicio, comboEspectador));
        panelCampos.add(new JLabel("Función:"));
        panelCampos.add(comboFuncion);
        panelCampos.add(new JLabel("Butacas ocupadas:"));
        panelCampos.add(labelOcupadas);
        panelCampos.add(new JLabel("Número de butaca:"));
        panelCampos.add(campoButaca);
        panelCampos.add(new JLabel("Tarifa:"));
        panelCampos.add(comboTarifa);

        Runnable mostrarOcupadas = () -> {
            FuncionPresencial f = ComponentesUI.seleccionado(comboFuncion);
            if (f == null) return;
            List<Integer> ocupadas = f.getButacasOcupadas();
            labelOcupadas.setText((ocupadas.isEmpty() ? "ninguna" : ocupadas.toString())
                    + " (" + f.getSala().getCantidadButacas() + " butacas)");
        };
        comboFuncion.addActionListener(e -> mostrarOcupadas.run());
        mostrarOcupadas.run();

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoButaca.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dispose());

        // CORRECCION S7: que la butaca exista en la sala y esté libre lo valida FuncionPresencial, antes este control estaba repetido acá.
        btnAceptar.addActionListener(e -> {
            try {
                int butaca = LectorCampos.entero(campoButaca, "Número de butaca");
                FuncionPresencial funcion = ComponentesUI.seleccionado(comboFuncion);
                ultimaEntrada = ventaServicio.venderEntradaPresencial(ComponentesUI.seleccionado(comboEspectador),
                        funcion, butaca, ComponentesUI.seleccionado(comboTarifa));
                mostrarOcupadas.run();

                int otraEntrada = JOptionPane.showConfirmDialog(
                        this,
                        "Entrada vendida correctamente (" + ComponentesUI.formatearPrecio(ultimaEntrada.getPrecio())
                                + ").\n¿Desea comprar otra butaca para esta función?",
                        "Entrada vendida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (otraEntrada == JOptionPane.YES_OPTION) {
                    campoButaca.setText("");
                    labelError.setText(" ");
                    campoButaca.requestFocus();
                } else {
                    dispose();
                }
            } catch (CampoInvalidoException ex) {
                labelError.setText(ex.getMessage());
                ex.getCampo().requestFocus();
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
        return ultimaEntrada != null;
    }

    public Entrada getUltimaEntrada() {
        return ultimaEntrada;
    }
}
