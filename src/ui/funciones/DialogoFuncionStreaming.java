package ui.funciones;

import excepciones.FestivalException;
import modelo.Edicion;
import modelo.Funcion;
import modelo.FuncionStreaming;
import modelo.Pelicula;
import servicios.FestivalServicio;
import servicios.FuncionServicio;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.LectorCampos;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

// CORRECCION S8: el diálogo salió de PanelFunciones a su propia clase y usa LectorCampos.
public class DialogoFuncionStreaming extends JDialog {
    private Funcion funcionCreada;

    public DialogoFuncionStreaming(Window padre, FuncionServicio funcionServicio, FestivalServicio festivalServicio) {
        super(padre, "Agregar función streaming", ModalityType.APPLICATION_MODAL);
        setSize(460, 340);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // CORRECCION I6/S10: solo películas que compiten en una edición, guardadas como objetos en el combo.
        JComboBox<Pelicula> comboPelicula = ComponentesUI.crearComboPeliculas(festivalServicio.peliculasQueCompiten());
        JLabel labelEdicion = new JLabel();
        labelEdicion.setFont(new Font("SansSerif", Font.ITALIC, 12));
        JTextField campoEnlace = new JTextField();
        JTextField campoCapacidad = new JTextField();
        JTextField campoFecha = new JTextField();
        JTextField campoHorario = new JTextField();

        JPanel panelCampos = ComponentesUI.crearPanelCampos(6);
        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Edición:"));
        panelCampos.add(labelEdicion);
        panelCampos.add(new JLabel("Enlace:"));
        panelCampos.add(campoEnlace);
        panelCampos.add(new JLabel("Capacidad virtual (max " + FuncionStreaming.CAPACIDAD_MAXIMA + "):"));
        panelCampos.add(campoCapacidad);
        panelCampos.add(new JLabel("Fecha (YYYY-MM-DD):"));
        panelCampos.add(campoFecha);
        panelCampos.add(new JLabel("Horario (HH:MM):"));
        panelCampos.add(campoHorario);

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

        campoEnlace.addActionListener(e -> campoCapacidad.requestFocus());
        campoCapacidad.addActionListener(e -> campoFecha.requestFocus());
        campoFecha.addActionListener(e -> campoHorario.requestFocus());
        campoHorario.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dispose());

        /* CORRECCION I9/S7: el rango de la capacidad lo valida FuncionStreaming (antes solo este diálogo)
        y las reglas de fecha y edición las controla FuncionServicio.*/
        btnAceptar.addActionListener(e -> {
            try {
                String enlace = LectorCampos.texto(campoEnlace, "Enlace");
                int capacidad = LectorCampos.entero(campoCapacidad, "Capacidad virtual");
                LocalDate fecha = LectorCampos.fecha(campoFecha, "Fecha");
                LocalTime horario = LectorCampos.hora(campoHorario, "Horario");
                funcionCreada = funcionServicio.programarFuncionStreaming(ComponentesUI.seleccionado(comboPelicula),
                        enlace, capacidad, fecha, horario);
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
