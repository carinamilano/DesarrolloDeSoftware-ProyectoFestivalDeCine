package ui.entradas;

import excepciones.FestivalException;
import modelo.Abono;
import modelo.Entrada;
import modelo.Funcion;
import servicios.AbonoServicio;
import servicios.FuncionServicio;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.LectorCampos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// CORRECCION S8: el diálogo de reserva salió de PanelAbonos a su propia clase.
public class DialogoReservaAbono extends JDialog {
    private Entrada reserva;

    public DialogoReservaAbono(Window padre, AbonoServicio abonoServicio, FuncionServicio funcionServicio, Abono abono) {
        super(padre, "Reservar con " + abono.getCodigo(), ModalityType.APPLICATION_MODAL);
        setSize(560, 280);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // CORRECCION S7/I2: qué funciones se pueden reservar lo decide AbonoServicio (cubiertas y no comenzadas).
        JComboBox<Funcion> comboFuncion = ComponentesUI.crearComboFunciones(abonoServicio.funcionesReservables(abono));
        JLabel labelOcupadas = new JLabel();
        labelOcupadas.setForeground(new Color(150, 50, 50));
        labelOcupadas.setFont(new Font("SansSerif", Font.ITALIC, 12));
        JTextField campoButaca = new JTextField();
        JPanel panelCampos = ComponentesUI.crearPanelCampos(4);
        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(new JLabel(abono.getEspectador().getNombre() + " (" + abono.getTipo() + ")"));
        panelCampos.add(new JLabel("Función:"));
        panelCampos.add(comboFuncion);
        panelCampos.add(new JLabel("Butacas ocupadas:"));
        panelCampos.add(labelOcupadas);
        panelCampos.add(new JLabel("Número de butaca:"));
        panelCampos.add(campoButaca);

        // CORRECCION S3: la función responde si necesita butaca; las ocupadas se piden a la función presencial buscándola por código, sin instanceof ni casts.
        Runnable actualizar = () -> {
            Funcion f = ComponentesUI.seleccionado(comboFuncion);
            if (f == null) return;
            if (f.requiereButaca()) {
                List<Integer> ocupadas = funcionServicio.buscarFuncionPresencial(f.getCodigo()).getButacasOcupadas();
                labelOcupadas.setText(ocupadas.isEmpty() ? "ninguna" : ocupadas.toString());
                campoButaca.setEnabled(true);
            } else {
                labelOcupadas.setText("(streaming, sin butaca)");
                campoButaca.setText("");
                campoButaca.setEnabled(false);
            }
        };
        comboFuncion.addActionListener(e -> actualizar.run());
        actualizar.run();

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Reservar");
        JButton btnCancelar = new JButton("Cancelar");

        campoButaca.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dispose());

        btnAceptar.addActionListener(e -> {
            Funcion funcion = ComponentesUI.seleccionado(comboFuncion);
            try {
                int butaca = funcion.requiereButaca() ? LectorCampos.entero(campoButaca, "Número de butaca") : 0;
                reserva = abonoServicio.reservar(abono, funcion, butaca);
                dispose();
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
        return reserva != null;
    }

    public Entrada getReserva() {
        return reserva;
    }
}
