package ui.entradas;

import modelo.Espectador;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PanelEspectadores extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelEspectadores(VentanaPrincipal ventana) {
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

        JLabel titulo = new JLabel("Gestión de Espectadores");
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
        String[] columnas = {"Nombre", "Email", "Nacionalidad", "Entradas compradas"};
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

        JButton btnAgregar = new JButton("Agregar espectador");
        btnAgregar.addActionListener(e -> agregarEspectador());

        panelBotones.add(btnAgregar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Espectador> espectadores = ventana.getEntradaServicio().getEspectadores();
        for (Espectador e : espectadores) {
            modeloTabla.addRow(new Object[]{
                    e.getNombre(),
                    e.getEmail(),
                    e.getNacionalidad(),
                    e.getEntradas().size()
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

    private void agregarEspectador() {
        if (mostrarDialogoAlta(ventana) != null) {
            cargarDatos();
        }
    }

    /**
     * Si no hay espectadores registrados, ofrece dar de alta uno en el momento.
     * Devuelve true si hay al menos un espectador para continuar con la venta.
     */
    public static boolean asegurarEspectadores(VentanaPrincipal ventana, String titulo) {
        if (!ventana.getEntradaServicio().getEspectadores().isEmpty()) return true;
        int opcion = JOptionPane.showConfirmDialog(ventana,
                "No hay espectadores registrados. ¿Desea dar de alta uno ahora?",
                titulo, JOptionPane.YES_NO_OPTION);
        return opcion == JOptionPane.YES_OPTION && mostrarDialogoAlta(ventana) != null;
    }

    /**
     * Arma el combo de espectadores junto a un botón "Nuevo..." que permite
     * dar de alta un espectador sin salir de la pantalla de venta.
     */
    public static JPanel crearSelectorEspectador(VentanaPrincipal ventana, JComboBox<String> combo) {
        for (Espectador e : ventana.getEntradaServicio().getEspectadores()) {
            combo.addItem(e.getNombre());
        }
        JButton btnNuevo = new JButton("Nuevo...");
        btnNuevo.setToolTipText("Dar de alta un espectador nuevo");
        btnNuevo.addActionListener(e -> {
            String nuevo = mostrarDialogoAlta(ventana);
            if (nuevo != null) {
                combo.addItem(nuevo);
                combo.setSelectedItem(nuevo);
            }
        });
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(combo, BorderLayout.CENTER);
        panel.add(btnNuevo, BorderLayout.EAST);
        return panel;
    }

    /**
     * Muestra el diálogo de alta de espectador. Se reutiliza desde otras pantallas
     * (por ejemplo, la compra de abonos). Devuelve el nombre del espectador
     * registrado, o null si se canceló.
     */
    public static String mostrarDialogoAlta(VentanaPrincipal ventana) {
        String[] registrado = {null};
        JDialog dialog = new JDialog(ventana, "Agregar espectador", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JTextField campoNombre = new JTextField();
        JTextField campoEmail = new JTextField();
        JTextField campoNacionalidad = new JTextField();

        panelCampos.add(new JLabel("Nombre:"));
        panelCampos.add(campoNombre);
        panelCampos.add(new JLabel("Email:"));
        panelCampos.add(campoEmail);
        panelCampos.add(new JLabel("Nacionalidad:"));
        panelCampos.add(campoNacionalidad);

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

        campoNombre.addActionListener(e -> campoEmail.requestFocus());
        campoEmail.addActionListener(e -> campoNacionalidad.requestFocus());
        campoNacionalidad.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            String email = campoEmail.getText().trim();
            String nacionalidad = campoNacionalidad.getText().trim();

            if (nombre.isEmpty()) {
                labelError.setText("El nombre es obligatorio.");
                campoNombre.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                labelError.setText("El email es obligatorio.");
                campoEmail.requestFocus();
                return;
            }
            if (nacionalidad.isEmpty()) {
                labelError.setText("La nacionalidad es obligatoria.");
                campoNacionalidad.requestFocus();
                return;
            }

            try {
                ventana.getEntradaServicio().registrarEspectador(nombre, email, nacionalidad);
                registrado[0] = nombre;
                ventana.setEstado("Espectador agregado: " + nombre);
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
        return registrado[0];
    }
}