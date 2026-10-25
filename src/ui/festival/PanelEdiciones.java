package ui.festival;

import excepciones.FestivalException;
import modelo.Edicion;
import servicios.FestivalServicio;
import ui.VentanaPrincipal;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;

public class PanelEdiciones extends JPanel {

    private VentanaPrincipal ventana;
    private FestivalServicio festivalServicio;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelEdiciones(VentanaPrincipal ventana, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.festivalServicio = festivalServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirTabla();
        construirPanelBotones();

        cargarDatos();
    }

    private void construirTabla() {
        modeloTabla = ComponentesUI.crearModeloTabla("Año", "Ciudad", "Fecha inicio (AAAA-MM-DD)", "Fecha fin (AAAA-MM-DD)");
        JTable tabla = ComponentesUI.crearTabla(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        add(ComponentesUI.crearEncabezado("Gestión de Ediciones", ComponentesUI.crearBuscador(sorter)), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregar = new JButton("Agregar edición");
        btnAgregar.addActionListener(e -> agregarEdicion());

        panelBotones.add(btnAgregar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Edicion e : festivalServicio.getEdiciones()) {
            modeloTabla.addRow(new Object[]{
                    e.getAnio(),
                    e.getCiudad(),
                    e.getFechaInicio().toString(),
                    e.getFechaFin().toString()
            });
        }
    }

    private void agregarEdicion() {
        JDialog dialog = new JDialog(ventana, "Agregar edición", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = ComponentesUI.crearPanelCampos(4);
        JTextField campoAnio = new JTextField();
        JTextField campoCiudad = new JTextField();
        JTextField campoFechaInicio = new JTextField();
        JTextField campoFechaFin = new JTextField();

        panelCampos.add(new JLabel("Año (" + Edicion.ANIO_MINIMO + "-" + Edicion.ANIO_MAXIMO + "):"));
        panelCampos.add(campoAnio);
        panelCampos.add(new JLabel("Ciudad:"));
        panelCampos.add(campoCiudad);
        panelCampos.add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
        panelCampos.add(campoFechaInicio);
        panelCampos.add(new JLabel("Fecha fin (YYYY-MM-DD):"));
        panelCampos.add(campoFechaFin);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoAnio.addActionListener(e -> campoCiudad.requestFocus());
        campoCiudad.addActionListener(e -> campoFechaInicio.requestFocus());
        campoFechaInicio.addActionListener(e -> campoFechaFin.requestFocus());
        campoFechaFin.addActionListener(e -> btnAceptar.doClick());

        btnCancelar.addActionListener(e -> dialog.dispose());

        /* CORRECCION S7/I9: la interfaz solo controla el formato. El rango del año y las fechas
        (que correspondan al año y que la fecha fin no sea anterior) los valida Edicion.*/
        btnAceptar.addActionListener(e -> {
            try {
                int anio = LectorCampos.entero(campoAnio, "Año");
                String ciudad = LectorCampos.texto(campoCiudad, "Ciudad");
                LocalDate fechaInicio = LectorCampos.fecha(campoFechaInicio, "Fecha inicio");
                LocalDate fechaFin = LectorCampos.fecha(campoFechaFin, "Fecha fin");
                festivalServicio.registrarEdicion(anio, ciudad, fechaInicio, fechaFin);
                cargarDatos();
                ventana.setEstado("Edición agregada: " + anio + " - " + ciudad);
                dialog.dispose();
            } catch (CampoInvalidoException ex) {
                labelError.setText(ex.getMessage());
                ex.getCampo().requestFocus();
            } catch (FestivalException ex) {
                labelError.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                labelError.setText(ComponentesUI.errorInesperado(ex));
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(ComponentesUI.crearPanelSur(labelError, btnAceptar, btnCancelar), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
