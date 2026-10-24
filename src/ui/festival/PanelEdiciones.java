package ui.festival;

import modelo.Edicion;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PanelEdiciones extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelEdiciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirTabla();
        construirPanelBotones();

        cargarDatos();
    }

    private void construirPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));

        JLabel titulo = new JLabel("Gestión de Ediciones");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel labelBusqueda = new JLabel("Buscar:");
        campoBusqueda = new JTextField(20);
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        panelBusqueda.add(labelBusqueda);
        panelBusqueda.add(campoBusqueda);

        panelSuperior.add(titulo, BorderLayout.WEST);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);
    }

    private void construirTabla() {
        String[] columnas = {"Año", "Ciudad", "Fecha inicio (AAAA-MM-DD)", "Fecha fin (AAAA-MM-DD)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
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
        List<Edicion> ediciones = ventana.getFestivalServicio().getEdiciones();
        for (Edicion e : ediciones) {
            modeloTabla.addRow(new Object[]{
                    e.getAnio(),
                    e.getCiudad(),
                    e.getFechaInicio().toString(),
                    e.getFechaFin().toString()
            });
        }
    }

    private void filtrar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void agregarEdicion() {
        JDialog dialog = new JDialog(ventana, "Agregar edición", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JTextField campoAnio = new JTextField();
        JTextField campoCiudad = new JTextField();
        JTextField campoFechaInicio = new JTextField();
        JTextField campoFechaFin = new JTextField();

        panelCampos.add(new JLabel("Año (2001-2030):"));
        panelCampos.add(campoAnio);
        panelCampos.add(new JLabel("Ciudad:"));
        panelCampos.add(campoCiudad);
        panelCampos.add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
        panelCampos.add(campoFechaInicio);
        panelCampos.add(new JLabel("Fecha fin (YYYY-MM-DD):"));
        panelCampos.add(campoFechaFin);

        JLabel labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        campoAnio.addActionListener(e -> campoCiudad.requestFocus());
        campoCiudad.addActionListener(e -> campoFechaInicio.requestFocus());
        campoFechaInicio.addActionListener(e -> campoFechaFin.requestFocus());
        campoFechaFin.addActionListener(e -> btnAceptar.doClick());

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String anioStr = campoAnio.getText().trim();
            String ciudad = campoCiudad.getText().trim();
            String fechaInicioStr = campoFechaInicio.getText().trim();
            String fechaFinStr = campoFechaFin.getText().trim();

            if (anioStr.isEmpty()) {
                labelError.setText("El año es obligatorio.");
                campoAnio.requestFocus();
                return;
            }

            int anio;
            try {
                anio = Integer.parseInt(anioStr);
                if (anio <= 2000 || anio > 2030) {
                    labelError.setText("El año debe estar entre 2001 y 2030.");
                    campoAnio.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                labelError.setText("El año debe ser un número entero.");
                campoAnio.requestFocus();
                return;
            }

            if (ciudad.isEmpty()) {
                labelError.setText("La ciudad es obligatoria.");
                campoCiudad.requestFocus();
                return;
            }

            if (fechaInicioStr.isEmpty()) {
                labelError.setText("La fecha de inicio es obligatoria.");
                campoFechaInicio.requestFocus();
                return;
            }

            LocalDate fechaInicio;
            try {
                fechaInicio = LocalDate.parse(fechaInicioStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Fecha inicio incorrecta. Use YYYY-MM-DD. Ej: " + anio + "-10-15");
                campoFechaInicio.requestFocus();
                return;
            }

            if (fechaInicio.getYear() != anio) {
                labelError.setText("La fecha inicio debe corresponder al año " + anio + ".");
                campoFechaInicio.requestFocus();
                return;
            }

            if (fechaFinStr.isEmpty()) {
                labelError.setText("La fecha fin es obligatoria.");
                campoFechaFin.requestFocus();
                return;
            }

            LocalDate fechaFin;
            try {
                fechaFin = LocalDate.parse(fechaFinStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Fecha fin incorrecta. Use YYYY-MM-DD. Ej: " + anio + "-10-31");
                campoFechaFin.requestFocus();
                return;
            }

            if (fechaFin.getYear() != anio) {
                labelError.setText("La fecha fin debe corresponder al año " + anio + ".");
                campoFechaFin.requestFocus();
                return;
            }

            if (fechaFin.isBefore(fechaInicio)) {
                labelError.setText("La fecha fin debe ser posterior a la fecha inicio.");
                campoFechaFin.requestFocus();
                return;
            }

            try {
                ventana.getFestivalServicio().registrarEdicion(anio, ciudad, fechaInicio, fechaFin);
                cargarDatos();
                ventana.setEstado("Edición agregada: " + anio + " - " + ciudad);
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}