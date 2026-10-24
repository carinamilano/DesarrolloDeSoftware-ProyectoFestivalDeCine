package ui.peliculas;

import modelo.Pelicula;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PanelPeliculas extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelPeliculas(VentanaPrincipal ventana) {
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

        JLabel titulo = new JLabel("Gestión de Películas");
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
        String[] columnas = {"Título", "Género", "Duración (min)", "Director", "Promedio"};
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

        JButton btnAgregar = new JButton("Agregar película");
        btnAgregar.addActionListener(e -> agregarPelicula());

        JButton btnEliminar = new JButton("Eliminar película");
        btnEliminar.addActionListener(e -> eliminarPelicula());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Pelicula> peliculas = ventana.getPeliculaServicio().getPeliculas();
        for (Pelicula p : peliculas) {
            modeloTabla.addRow(new Object[]{
                    p.getTitulo(),
                    p.getGenero(),
                    p.getDuracion(),
                    p.getDirector().getNombre(),
                    String.format("%.2f", p.calcularPromedio())
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

    private void agregarPelicula() {
        List<String> directores = new java.util.ArrayList<>();
        for (modelo.Director d : ventana.getPeliculaServicio().getDirectores()) {
            directores.add(d.getNombre());
        }
        if (directores.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay directores registrados. Agregue un director primero.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(ventana, "Agregar película", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JTextField campoTitulo = new JTextField();
        JComboBox<String> comboGenero = new JComboBox<>(new String[]{
                "Drama", "Comedia", "Thriller", "Ciencia Ficcion",
                "Terror", "Animacion", "Documental", "Musical", "Romance", "Accion"
        });
        JTextField campoDuracion = new JTextField();
        JComboBox<String> comboDirector = new JComboBox<>(directores.toArray(new String[0]));

        panelCampos.add(new JLabel("Título:"));
        panelCampos.add(campoTitulo);
        panelCampos.add(new JLabel("Género:"));
        panelCampos.add(comboGenero);
        panelCampos.add(new JLabel("Duración (min):"));
        panelCampos.add(campoDuracion);
        panelCampos.add(new JLabel("Director:"));
        panelCampos.add(comboDirector);

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

        campoTitulo.addActionListener(e -> campoDuracion.requestFocus());
        campoDuracion.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String titulo = campoTitulo.getText().trim();
            String duracionStr = campoDuracion.getText().trim();
            String genero = (String) comboGenero.getSelectedItem();
            String director = (String) comboDirector.getSelectedItem();

            if (titulo.isEmpty()) {
                labelError.setText("El título es obligatorio.");
                campoTitulo.requestFocus();
                return;
            }

            if (duracionStr.isEmpty()) {
                labelError.setText("La duración es obligatoria.");
                campoDuracion.requestFocus();
                return;
            }

            try {
                int duracion = Integer.parseInt(duracionStr);
                if (duracion <= 0) {
                    labelError.setText("La duración debe ser mayor a 0.");
                    campoDuracion.requestFocus();
                    return;
                }
                ventana.getPeliculaServicio().registrarPelicula(titulo, genero, duracion, director);
                cargarDatos();
                ventana.setEstado("Película agregada: " + titulo);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                labelError.setText("La duración debe ser un número entero.");
                campoDuracion.requestFocus();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void eliminarPelicula() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione una película de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaSeleccionada);
        String titulo = (String) modeloTabla.getValueAt(filaModelo, 0);

        int confirmacion = JOptionPane.showConfirmDialog(ventana,
                "¿Está seguro que desea eliminar la película \"" + titulo + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                ventana.getPeliculaServicio().eliminarPelicula(titulo, ventana.getFestivalServicio().getEdiciones());
                cargarDatos();
                ventana.setEstado("Película eliminada: " + titulo);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventana, ex.getMessage(),
                        "No se puede eliminar", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}