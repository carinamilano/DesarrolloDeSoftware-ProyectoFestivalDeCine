package ui.evaluaciones;

import modelo.Edicion;
import modelo.Pelicula;
import modelo.Seccion;
import modelo.TipoSeccion;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelEvaluaciones extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tablaEvaluaciones;
    private DefaultTableModel modeloEvaluaciones;
    private JTextArea areaGanadores;

    public PanelEvaluaciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirPanelCentral();
        construirPanelBotones();

        cargarEvaluaciones();
        mostrarGanadores();
    }

    private void construirPanelSuperior() {
        JLabel titulo = new JLabel("Evaluaciones y Ganadores");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(450);

        // --- Tabla de evaluaciones (izquierda) ---
        String[] columnas = {"Película", "Jurado", "Puntaje"};
        modeloEvaluaciones = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEvaluaciones = new JTable(modeloEvaluaciones);
        tablaEvaluaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEvaluaciones.getTableHeader().setReorderingAllowed(false);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Evaluaciones registradas"));
        panelTabla.add(new JScrollPane(tablaEvaluaciones), BorderLayout.CENTER);

        // --- Panel de ganadores (derecha) ---
        JPanel panelGanadores = new JPanel(new BorderLayout());
        panelGanadores.setBorder(BorderFactory.createTitledBorder("Ganadores por sección"));

        areaGanadores = new JTextArea();
        areaGanadores.setEditable(false);
        areaGanadores.setFont(new Font("SansSerif", Font.PLAIN, 13));
        areaGanadores.setMargin(new Insets(8, 8, 8, 8));

        panelGanadores.add(new JScrollPane(areaGanadores), BorderLayout.CENTER);

        splitPane.setLeftComponent(panelTabla);
        splitPane.setRightComponent(panelGanadores);

        add(splitPane, BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregarEvaluacion = new JButton("Agregar evaluación");
        btnAgregarEvaluacion.addActionListener(e -> agregarEvaluacion());

        panelBotones.add(btnAgregarEvaluacion);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarEvaluaciones() {
        modeloEvaluaciones.setRowCount(0);
        for (modelo.Evaluacion ev : ventana.getPeliculaServicio().getEvaluaciones()) {
            modeloEvaluaciones.addRow(new Object[]{
                    ev.getPelicula().getTitulo(),
                    ev.getJurado().getNombre(),
                    String.format("%.1f", ev.getPuntaje())
            });
        }
    }

    private void agregarEvaluacion() {
        List<modelo.Pelicula> peliculas = ventana.getPeliculaServicio().getPeliculas();
        List<modelo.Jurado> jurados = ventana.getPeliculaServicio().getJurados();

        if (peliculas.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "Se necesitan películas registradas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombresPeliculas = peliculas.stream()
                .map(Pelicula::getTitulo).toArray(String[]::new);

        // Armamos el combo de jurados con la opción "Nuevo jurado" al principio
        String NUEVO_JURADO = "-- Nuevo jurado --";
        String[] nombresJurados = new String[jurados.size() + 1];
        nombresJurados[0] = NUEVO_JURADO;
        for (int i = 0; i < jurados.size(); i++) {
            nombresJurados[i + 1] = jurados.get(i).getNombre();
        }

        JDialog dialog = new JDialog(ventana, "Agregar evaluación", true);
        dialog.setSize(400, 260);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboPelicula = new JComboBox<>(nombresPeliculas);
        JComboBox<String> comboJurado = new JComboBox<>(nombresJurados);
        JTextField campoPuntaje = new JTextField();

        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Jurado:"));
        panelCampos.add(comboJurado);
        panelCampos.add(new JLabel("Puntaje (0-10):"));
        panelCampos.add(campoPuntaje);

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

        campoPuntaje.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String puntajeStr = campoPuntaje.getText().trim();
            String tituloPelicula = (String) comboPelicula.getSelectedItem();
            String nombreJuradoSeleccionado = (String) comboJurado.getSelectedItem();

            if (puntajeStr.isEmpty()) {
                labelError.setText("El puntaje es obligatorio.");
                campoPuntaje.requestFocus();
                return;
            }

            double puntaje;
            try {
                puntaje = Double.parseDouble(puntajeStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                labelError.setText("El puntaje debe ser un número. Ej: 8.5");
                campoPuntaje.requestFocus();
                return;
            }

            // Si eligió "-- Nuevo jurado --", pedimos los datos
            String nombreJuradoFinal = nombreJuradoSeleccionado;
            if (NUEVO_JURADO.equals(nombreJuradoSeleccionado)) {
                nombreJuradoFinal = pedirDatosNuevoJurado(dialog);
                if (nombreJuradoFinal == null) {
                    // Canceló el formulario de nuevo jurado
                    return;
                }
            }

            try {
                ventana.getPeliculaServicio().registrarEvaluacion(nombreJuradoFinal, tituloPelicula, puntaje);
                cargarEvaluaciones();
                mostrarGanadores();
                ventana.setEstado("Evaluación agregada para: " + tituloPelicula);
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Abre un sub-diálogo para ingresar los datos del nuevo jurado.
// Devuelve el nombre del jurado creado, o null si el usuario canceló.
    private String pedirDatosNuevoJurado(JDialog dialogPadre) {
        JDialog dialogJurado = new JDialog(dialogPadre, "Nuevo jurado", true);
        dialogJurado.setSize(380, 240);
        dialogJurado.setLocationRelativeTo(dialogPadre);
        dialogJurado.setLayout(new BorderLayout(10, 10));

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

        // Navegación con Enter entre campos
        campoNombre.addActionListener(e -> campoEmail.requestFocus());
        campoEmail.addActionListener(e -> campoNacionalidad.requestFocus());
        campoNacionalidad.addActionListener(e -> btnAceptar.doClick());

        // Usamos un array para poder capturar el resultado desde dentro del lambda
        String[] resultado = {null};

        btnCancelar.addActionListener(e -> dialogJurado.dispose());

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
                ventana.getPeliculaServicio().registrarJurado(nombre, email, nacionalidad);
                resultado[0] = nombre;
                dialogJurado.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialogJurado.add(panelCampos, BorderLayout.CENTER);
        dialogJurado.add(panelSur, BorderLayout.SOUTH);
        dialogJurado.setVisible(true);

        return resultado[0];
    }

    private void mostrarGanadores() {
        List<Edicion> ediciones = ventana.getFestivalServicio().getEdiciones();

        if (ediciones.isEmpty()) {
            areaGanadores.setText("No hay ediciones registradas.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (Edicion edicion : ediciones) {
            sb.append("Edición ").append(edicion.getAnio())
                    .append(" — ").append(edicion.getCiudad()).append("\n");
            sb.append("─────────────────────────────\n");

            List<Seccion> secciones = edicion.getSecciones();
            if (secciones.isEmpty()) {
                sb.append("  Sin secciones registradas.\n");
            } else {
                for (Seccion seccion : secciones) {
                    sb.append("  ").append(seccion.getTipo()).append(":\n");
                    Pelicula ganadora = ventana.getPeliculaServicio().determinarGanadora(seccion);
                    if (ganadora != null) {
                        sb.append("    🏆 ").append(ganadora.getTitulo())
                                .append(" (promedio: ")
                                .append(String.format("%.2f", ganadora.calcularPromedio()))
                                .append(")\n");
                    } else {
                        sb.append("    Sin películas evaluadas.\n");
                    }
                }
            }
            sb.append("\n");
        }

        areaGanadores.setText(sb.toString());
        areaGanadores.setCaretPosition(0);
        ventana.setEstado("Ganadores calculados correctamente.");
    }
}