package ui.peliculas;

import modelo.Actor;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PanelActores extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelActores(VentanaPrincipal ventana) {
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

        JLabel titulo = new JLabel("Gestión de Actores");
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
        String[] columnas = {"Nombre", "Email", "Nacionalidad"};
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

        JButton btnAgregar = new JButton("Agregar actor");
        btnAgregar.addActionListener(e -> agregarActor());

        JButton btnAsociar = new JButton("Asociar actor a película");
        btnAsociar.addActionListener(e -> asociarActorAPelicula());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnAsociar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Actor> actores = ventana.getPeliculaServicio().getActores();
        for (Actor a : actores) {
            modeloTabla.addRow(new Object[]{
                    a.getNombre(),
                    a.getEmail(),
                    a.getNacionalidad()
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

    private void agregarActor() {
        JDialog dialog = new JDialog(ventana, "Agregar actor", true);
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
                ventana.getPeliculaServicio().registrarActor(nombre, email, nacionalidad);
                cargarDatos();
                ventana.setEstado("Actor agregado: " + nombre);
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void asociarActorAPelicula() {
        List<Actor> actores = ventana.getPeliculaServicio().getActores();
        List<modelo.Pelicula> peliculas = ventana.getPeliculaServicio().getPeliculas();

        if (actores.isEmpty() || peliculas.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "Se necesitan actores y películas registrados.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombresActores = actores.stream()
                .map(Actor::getNombre).toArray(String[]::new);
        String[] nombresPeliculas = peliculas.stream()
                .map(modelo.Pelicula::getTitulo).toArray(String[]::new);

        JDialog dialog = new JDialog(ventana, "Asociar actor a película", true);
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboActor = new JComboBox<>(nombresActores);
        JComboBox<String> comboPelicula = new JComboBox<>(nombresPeliculas);

        panelCampos.add(new JLabel("Actor:"));
        panelCampos.add(comboActor);
        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);

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

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String nombreActor = (String) comboActor.getSelectedItem();
            String tituloPelicula = (String) comboPelicula.getSelectedItem();

            try {
                ventana.getPeliculaServicio().asociarActorAPelicula(nombreActor, tituloPelicula);
                cargarDatos();
                ventana.setEstado("Actor " + nombreActor + " asociado a " + tituloPelicula);
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