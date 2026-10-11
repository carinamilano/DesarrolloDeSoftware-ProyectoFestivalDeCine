package ui.festival;

import modelo.*;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelSecciones extends JPanel {

    private VentanaPrincipal ventana;
    private JComboBox<Integer> comboEdicion;
    private JTable tablaSecciones;
    private DefaultTableModel modeloSecciones;
    private JTable tablaPeliculas;
    private DefaultTableModel modeloPeliculas;
    private JButton btnAgregar;

    public PanelSecciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirPanelCentral();
        construirPanelBotones();

        cargarSecciones();
        actualizarBotonAgregar();
    }

    private void construirPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));

        JLabel titulo = new JLabel("Gestión de Secciones");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelEdicion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        comboEdicion = new JComboBox<>();
        for (Edicion e : ventana.getFestivalServicio().getEdiciones()) {
            comboEdicion.addItem(e.getAnio());
        }
        comboEdicion.addActionListener(e -> {
            cargarSecciones();
            actualizarBotonAgregar();
        });
        panelEdicion.add(new JLabel("Edición:"));
        panelEdicion.add(comboEdicion);

        panelSuperior.add(titulo, BorderLayout.WEST);
        panelSuperior.add(panelEdicion, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // --- Secciones de la edición elegida (arriba) ---
        String[] columnasSecciones = {"Sección", "Premio", "Descripción", "Películas", "Ganadora (jurado)"};
        modeloSecciones = new DefaultTableModel(columnasSecciones, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaSecciones = new JTable(modeloSecciones);
        tablaSecciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSecciones.getTableHeader().setReorderingAllowed(false);
        tablaSecciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarPeliculas();
        });

        JPanel panelSecciones = new JPanel(new BorderLayout());
        panelSecciones.setBorder(BorderFactory.createTitledBorder("Secciones de la edición"));
        panelSecciones.add(new JScrollPane(tablaSecciones), BorderLayout.CENTER);

        // --- Películas de la sección seleccionada (abajo) ---
        String[] columnasPeliculas = {"Título", "Género", "Director", "Promedio jurado"};
        modeloPeliculas = new DefaultTableModel(columnasPeliculas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPeliculas = new JTable(modeloPeliculas);
        tablaPeliculas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPeliculas.getTableHeader().setReorderingAllowed(false);

        JPanel panelPeliculas = new JPanel(new BorderLayout());
        panelPeliculas.setBorder(BorderFactory.createTitledBorder("Películas de la sección seleccionada"));
        panelPeliculas.add(new JScrollPane(tablaPeliculas), BorderLayout.CENTER);

        splitPane.setTopComponent(panelSecciones);
        splitPane.setBottomComponent(panelPeliculas);
        add(splitPane, BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        btnAgregar = new JButton("Agregar sección");
        btnAgregar.addActionListener(e -> agregarSeccion());

        JButton btnAsociar = new JButton("Asociar película");
        btnAsociar.addActionListener(e -> asociarPelicula());

        JButton btnQuitar = new JButton("Quitar película");
        btnQuitar.addActionListener(e -> quitarPelicula());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnAsociar);
        panelBotones.add(btnQuitar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private Edicion getEdicionSeleccionada() {
        Integer anio = (Integer) comboEdicion.getSelectedItem();
        if (anio == null) return null;
        return ventana.getFestivalServicio().buscarEdicion(anio);
    }

    private Seccion getSeccionSeleccionada() {
        Edicion edicion = getEdicionSeleccionada();
        int fila = tablaSecciones.getSelectedRow();
        if (edicion == null || fila < 0) return null;
        TipoSeccion tipo = (TipoSeccion) modeloSecciones.getValueAt(fila, 0);
        return edicion.buscarSeccion(tipo);
    }

    private void cargarSecciones() {
        modeloSecciones.setRowCount(0);
        modeloPeliculas.setRowCount(0);
        Edicion edicion = getEdicionSeleccionada();
        if (edicion == null) return;
        for (Seccion s : edicion.getSecciones()) {
            Pelicula ganadora = s.determinarGanadora();
            modeloSecciones.addRow(new Object[]{
                    s.getTipo(),
                    s.getPremio() != null ? s.getPremio().getNombre() : "-",
                    s.getPremio() != null ? s.getPremio().getDescripcion() : "-",
                    s.getPeliculas().size(),
                    ganadora != null ? ganadora.getTitulo() : "-"
            });
        }
    }

    private void cargarPeliculas() {
        modeloPeliculas.setRowCount(0);
        Seccion seccion = getSeccionSeleccionada();
        if (seccion == null) return;
        for (Pelicula p : seccion.getPeliculas()) {
            modeloPeliculas.addRow(new Object[]{
                    p.getTitulo(),
                    p.getGenero(),
                    p.getDirector().getNombre(),
                    String.format("%.2f", p.calcularPromedio())
            });
        }
    }

    // El botón solo se habilita si la edición elegida todavía no tiene todos los tipos de sección
    private void actualizarBotonAgregar() {
        Edicion edicion = getEdicionSeleccionada();
        boolean completa = edicion != null && edicion.getSecciones().size() >= TipoSeccion.values().length;
        btnAgregar.setEnabled(edicion != null && !completa);
        if (edicion == null) {
            btnAgregar.setToolTipText("No hay ediciones registradas.");
        } else if (completa) {
            btnAgregar.setToolTipText("La edición " + edicion.getAnio() + " ya tiene todas las secciones.");
        } else {
            btnAgregar.setToolTipText("Agregar una sección a la edición " + edicion.getAnio());
        }
    }

    private void seleccionarSeccion(TipoSeccion tipo) {
        for (int i = 0; i < modeloSecciones.getRowCount(); i++) {
            if (modeloSecciones.getValueAt(i, 0) == tipo) {
                tablaSecciones.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    // ========== AGREGAR SECCIÓN ==========

    private void agregarSeccion() {
        Edicion edicion = getEdicionSeleccionada();
        if (edicion == null) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay ediciones registradas. Registre una desde Festival > Ediciones.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Solo se ofrecen los tipos que la edición todavía no tiene
        List<TipoSeccion> disponibles = new ArrayList<>();
        for (TipoSeccion t : TipoSeccion.values()) {
            if (edicion.buscarSeccion(t) == null) disponibles.add(t);
        }
        if (disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "La edición " + edicion.getAnio() + " ya tiene todas las secciones.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(ventana, "Agregar sección - Edición " + edicion.getAnio(), true);
        dialog.setSize(440, 250);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<TipoSeccion> comboTipo = new JComboBox<>(disponibles.toArray(new TipoSeccion[0]));
        JTextField campoPremio = new JTextField();
        JTextField campoDescripcion = new JTextField();

        panelCampos.add(new JLabel("Tipo de sección:"));
        panelCampos.add(comboTipo);
        panelCampos.add(new JLabel("Nombre del premio:"));
        panelCampos.add(campoPremio);
        panelCampos.add(new JLabel("Descripción del premio:"));
        panelCampos.add(campoDescripcion);

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

        campoPremio.addActionListener(e -> campoDescripcion.requestFocus());
        campoDescripcion.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            TipoSeccion tipo = (TipoSeccion) comboTipo.getSelectedItem();
            try {
                ventana.getFestivalServicio().registrarSeccion(edicion.getAnio(), tipo,
                        campoPremio.getText().trim(), campoDescripcion.getText().trim());
                cargarSecciones();
                actualizarBotonAgregar();
                seleccionarSeccion(tipo);
                ventana.setEstado("Sección agregada: " + tipo + " (edición " + edicion.getAnio() + ")");
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ========== ASOCIAR PELÍCULA ==========

    private void asociarPelicula() {
        Edicion edicion = getEdicionSeleccionada();
        Seccion seccion = getSeccionSeleccionada();
        if (seccion == null) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione una sección de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Solo se ofrecen las películas que no están en la sección y no compiten en otra edición
        List<String> titulos = new ArrayList<>();
        for (Pelicula p : ventana.getPeliculaServicio().getPeliculas()) {
            Edicion suEdicion = ventana.getFestivalServicio().buscarEdicionDePelicula(p);
            boolean libre = suEdicion == null || suEdicion == edicion;
            if (libre && !seccion.getPeliculas().contains(p)) titulos.add(p.getTitulo());
        }
        if (titulos.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay películas disponibles: cada película compite en una sola edición\n"
                            + "y las que quedan ya están en esta sección o en otra edición.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String titulo = (String) JOptionPane.showInputDialog(ventana,
                "Película a asociar a " + seccion.getTipo() + ":",
                "Asociar película", JOptionPane.PLAIN_MESSAGE, null,
                titulos.toArray(), titulos.get(0));
        if (titulo == null) return;

        try {
            Pelicula pelicula = ventana.getPeliculaServicio().buscarPelicula(titulo);
            ventana.getFestivalServicio().asociarPeliculaASeccion(edicion.getAnio(), seccion.getTipo(), pelicula);
            TipoSeccion tipo = seccion.getTipo();
            cargarSecciones();
            seleccionarSeccion(tipo);
            ventana.setEstado("Película " + titulo + " asociada a " + tipo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========== QUITAR PELÍCULA ==========

    private void quitarPelicula() {
        Edicion edicion = getEdicionSeleccionada();
        Seccion seccion = getSeccionSeleccionada();
        int fila = tablaPeliculas.getSelectedRow();
        if (seccion == null || fila < 0) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione una sección y una película de la sección.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String titulo = (String) modeloPeliculas.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(ventana,
                "¿Quitar \"" + titulo + "\" de la sección " + seccion.getTipo() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            Pelicula pelicula = ventana.getPeliculaServicio().buscarPelicula(titulo);
            TipoSeccion tipo = seccion.getTipo();
            ventana.getFestivalServicio().quitarPeliculaDeSeccion(edicion.getAnio(), tipo, pelicula);
            cargarSecciones();
            seleccionarSeccion(tipo);
            ventana.setEstado("Película " + titulo + " quitada de " + tipo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
