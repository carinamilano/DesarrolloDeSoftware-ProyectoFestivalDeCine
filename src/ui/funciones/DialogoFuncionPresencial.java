package ui.funciones;

import excepciones.FestivalException;
import modelo.Edicion;
import modelo.Funcion;
import modelo.Pelicula;
import modelo.Sala;
import servicios.FestivalServicio;
import servicios.FuncionServicio;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.LectorCampos;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

/* CORRECCION S8: el diálogo salió de PanelFunciones a su propia clase y usa LectorCampos.
Antes las validaciones de fecha y horario estaban copiadas cuatro veces en el panel.*/
public class DialogoFuncionPresencial extends JDialog {
    private Funcion funcionCreada;

    public DialogoFuncionPresencial(Window padre, FuncionServicio funcionServicio, FestivalServicio festivalServicio) {
        super(padre, "Agregar función presencial", ModalityType.APPLICATION_MODAL);
        setSize(460, 320);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // CORRECCION I6/S10: solo películas que compiten en una edición, guardadas como objetos en el combo.
        JComboBox<Pelicula> comboPelicula = ComponentesUI.crearComboPeliculas(festivalServicio.peliculasQueCompiten());
        JComboBox<Sala> comboSala = new JComboBox<>(festivalServicio.getSalas().toArray(new Sala[0]));
        JLabel labelEdicion = new JLabel();
        labelEdicion.setFont(new Font("SansSerif", Font.ITALIC, 12));
        JTextField campoFecha = new JTextField();
        JTextField campoHorario = new JTextField();

        JPanel panelCampos = ComponentesUI.crearPanelCampos(5);
        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Edición:"));
        panelCampos.add(labelEdicion);
        panelCampos.add(new JLabel("Sala:"));
        panelCampos.add(comboSala);
        panelCampos.add(new JLabel("Fecha (YYYY-MM-DD):"));
        panelCampos.add(campoFecha);
        panelCampos.add(new JLabel("Horario (HH:MM):"));
        panelCampos.add(campoHorario);

        // Muestra entre qué fechas se puede programar la película elegida
        Runnable mostrarEdicion = () -> {
            Pelicula p = ComponentesUI.seleccionado(comboPelicula);
            if (p == null) {
                labelEdicion.setText("-");
                return;
            }
            Edicion e = festivalServicio.buscarEdicionDe(p);
            labelEdicion.setText(e.getAnio() + ": del " + e.getFechaInicio() + " al " + e.getFechaFin());
        };
        comboPelicula.addActionListener(e -> mostrarEdicion.run());
        mostrarEdicion.run();

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoFecha.addActionListener(e -> campoHorario.requestFocus());
        campoHorario.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dispose());

        /* CORRECCION S7: la interfaz solo valida el formato. "Fecha pasada", "fuera de la edición" y
        "sala ocupada" son reglas del negocio y las controla FuncionServicio.*/
        btnAceptar.addActionListener(e -> {
            try {
                LocalDate fecha = LectorCampos.fecha(campoFecha, "Fecha");
                LocalTime horario = LectorCampos.hora(campoHorario, "Horario");
                funcionCreada = funcionServicio.programarFuncionPresencial(ComponentesUI.seleccionado(comboPelicula),
                        ComponentesUI.seleccionado(comboSala), fecha, horario);
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
        return funcionCreada != null;
    }

    public Funcion getFuncionCreada() {
        return funcionCreada;
    }
}
