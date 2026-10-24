package ui.festival;

import modelo.Sala;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PanelSalas extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelSalas(VentanaPrincipal ventana) {
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

        JLabel titulo = new JLabel("Gestión de Salas");
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
        String[] columnas = {"Nombre", "Capacidad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Columnas más chicas
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
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
        List<Sala> salas = ventana.getFestivalServicio().getSalas();
        for (Sala s : salas) {
            modeloTabla.addRow(new Object[]{
                    s.getNombre(),
                    s.getCantidadButacas()
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

    private void agregarSala() {
        List<Sala> salas = ventana.getFestivalServicio().getSalas();

        JDialog dialog = new JDialog(ventana, "Agregar sala", true);
        dialog.setSize(350, 220);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JLabel labelNumero = new JLabel("Sala N°:");
        JTextField campoNumero = new JTextField();

        JLabel labelCapacidad = new JLabel("Capacidad:");
        JTextField campoCapacidad = new JTextField();

        panelCampos.add(labelNumero);
        panelCampos.add(campoNumero);
        panelCampos.add(labelCapacidad);
        panelCampos.add(campoCapacidad);

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

        campoNumero.addActionListener(e -> campoCapacidad.requestFocus());
        campoCapacidad.addActionListener(e -> btnAceptar.doClick());

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String numeroStr = campoNumero.getText().trim();
            String capacidadStr = campoCapacidad.getText().trim();

            if (numeroStr.isEmpty()) {
                labelError.setText("El número de sala es obligatorio.");
                campoNumero.requestFocus();
                return;
            }

            int numero;
            try {
                numero = Integer.parseInt(numeroStr);
                if (numero <= 0) {
                    labelError.setText("El número debe ser mayor a 0.");
                    campoNumero.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                labelError.setText("El número de sala debe ser un entero.");
                campoNumero.requestFocus();
                return;
            }

            String nombreSala = "Sala " + numero;
            for (Sala s : salas) {
                if (s.getNombre().equalsIgnoreCase(nombreSala)) {
                    labelError.setText("Ya existe la " + nombreSala + ".");
                    campoNumero.requestFocus();
                    return;
                }
            }

            if (capacidadStr.isEmpty()) {
                labelError.setText("La capacidad es obligatoria.");
                campoCapacidad.requestFocus();
                return;
            }

            try {
                int capacidad = Integer.parseInt(capacidadStr);
                if (capacidad <= 0) {
                    labelError.setText("La capacidad debe ser mayor a 0.");
                    campoCapacidad.requestFocus();
                    return;
                }
                ventana.getFestivalServicio().registrarSala(nombreSala, capacidad);
                cargarDatos();
                ventana.setEstado("Sala agregada: " + nombreSala);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                labelError.setText("La capacidad debe ser un número entero.");
                campoCapacidad.requestFocus();
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}