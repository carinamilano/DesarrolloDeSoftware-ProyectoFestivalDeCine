package ui.festival;

import excepciones.FestivalException;
import modelo.Sala;
import servicios.FestivalServicio;
import ui.VentanaPrincipal;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class PanelSalas extends JPanel {

    private VentanaPrincipal ventana;
    private FestivalServicio festivalServicio;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelSalas(VentanaPrincipal ventana, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.festivalServicio = festivalServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirTabla();
        construirPanelBotones();

        cargarDatos();
    }

    private void construirTabla() {
        modeloTabla = ComponentesUI.crearModeloTabla("Nombre", "Capacidad");
        JTable tabla = ComponentesUI.crearTabla(modeloTabla);

        // Columnas más chicas
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        add(ComponentesUI.crearEncabezado("Gestión de Salas", ComponentesUI.crearBuscador(sorter)), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregar = new JButton("Agregar sala");
        btnAgregar.addActionListener(e -> agregarSala());

        panelBotones.add(btnAgregar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Sala s : festivalServicio.getSalas()) {
            modeloTabla.addRow(new Object[]{
                    s.getNombre(),
                    s.getCantidadButacas()
            });
        }
    }

    private void agregarSala() {
        JDialog dialog = new JDialog(ventana, "Agregar sala", true);
        dialog.setSize(350, 220);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = ComponentesUI.crearPanelCampos(2);
        JTextField campoNumero = new JTextField();
        JTextField campoCapacidad = new JTextField();

        panelCampos.add(new JLabel("Sala N°:"));
        panelCampos.add(campoNumero);
        panelCampos.add(new JLabel("Capacidad:"));
        panelCampos.add(campoCapacidad);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoNumero.addActionListener(e -> campoCapacidad.requestFocus());
        campoCapacidad.addActionListener(e -> btnAceptar.doClick());

        btnCancelar.addActionListener(e -> dialog.dispose());

        /* CORRECCION I8/I9/S7: la sala repetida la controla FestivalServicio y la capacidad la valida Sala;
        antes las dos reglas estaban solo en este panel.*/
        btnAceptar.addActionListener(e -> {
            try {
                int numero = LectorCampos.entero(campoNumero, "Sala N°");
                if (numero <= 0) {
                    throw new CampoInvalidoException("El número de sala debe ser mayor a 0.", campoNumero);
                }
                int capacidad = LectorCampos.entero(campoCapacidad, "Capacidad");
                String nombreSala = "Sala " + numero;
                festivalServicio.registrarSala(nombreSala, capacidad);
                cargarDatos();
                ventana.setEstado("Sala agregada: " + nombreSala);
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
