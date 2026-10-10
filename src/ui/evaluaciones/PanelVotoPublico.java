package ui.evaluaciones;

import modelo.*;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelVotoPublico extends JPanel {

    private VentanaPrincipal ventana;
    private DefaultTableModel modeloVotos;
    private JTextArea areaPremio;

    public PanelVotoPublico(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirPanelCentral();
        construirPanelBotones();

        cargarVotos();
        mostrarPremioPublico();
    }

    private void construirPanelSuperior() {
        JLabel titulo = new JLabel("Voto del Público");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(450);

        // --- Tabla de películas con sus votos (izquierda) ---
        String[] columnas = {"Película", "Votos", "Promedio (1-5)"};
        modeloVotos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaVotos = new JTable(modeloVotos);
        tablaVotos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVotos.getTableHeader().setReorderingAllowed(false);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Votos por película"));
        panelTabla.add(new JScrollPane(tablaVotos), BorderLayout.CENTER);

        // --- Premio del público por edición (derecha) ---
        JPanel panelPremio = new JPanel(new BorderLayout());
        panelPremio.setBorder(BorderFactory.createTitledBorder("Premio del Público por edición"));

        areaPremio = new JTextArea();
        areaPremio.setEditable(false);
        areaPremio.setFont(new Font("SansSerif", Font.PLAIN, 13));
        areaPremio.setMargin(new Insets(8, 8, 8, 8));
        panelPremio.add(new JScrollPane(areaPremio), BorderLayout.CENTER);

        splitPane.setLeftComponent(panelTabla);
        splitPane.setRightComponent(panelPremio);
        add(splitPane, BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnVotar = new JButton("Registrar voto");
        btnVotar.addActionListener(e -> registrarVoto());

        panelBotones.add(btnVotar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarVotos() {
        modeloVotos.setRowCount(0);
        for (Pelicula p : ventana.getPeliculaServicio().getPeliculas()) {
            int cantidad = p.getVotosPublico().size();
            modeloVotos.addRow(new Object[]{
                    p.getTitulo(),
                    cantidad,
                    cantidad == 0 ? "-" : String.format("%.2f", p.calcularPromedioPublico())
            });
        }
    }

    private void registrarVoto() {
        List<Espectador> espectadores = ventana.getEntradaServicio().getEspectadores();
        if (espectadores.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay espectadores registrados.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombresEspectadores = espectadores.stream()
                .map(Espectador::getNombre).toArray(String[]::new);

        JDialog dialog = new JDialog(ventana, "Registrar voto del público", true);
        dialog.setSize(420, 240);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboEspectador = new JComboBox<>(nombresEspectadores);
        JComboBox<String> comboPelicula = new JComboBox<>();
        JComboBox<Integer> comboPuntaje = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});

        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(comboEspectador);
        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Puntaje (estrellas):"));
        panelCampos.add(comboPuntaje);

        // Solo se ofrecen las películas que el espectador vio y todavía no votó
        Runnable actualizarPeliculas = () -> {
            comboPelicula.removeAllItems();
            Espectador esp = espectadores.get(comboEspectador.getSelectedIndex());
            List<Pelicula> vistas = new ArrayList<>();
            for (Entrada en : esp.getEntradas()) {
                Pelicula p = en.getFuncion().getPelicula();
                if (!vistas.contains(p) && !p.fueVotadaPor(esp)) {
                    vistas.add(p);
                    comboPelicula.addItem(p.getTitulo());
                }
            }
        };
        comboEspectador.addActionListener(e -> actualizarPeliculas.run());
        actualizarPeliculas.run();

        JLabel labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Votar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String titulo = (String) comboPelicula.getSelectedItem();
            if (titulo == null) {
                labelError.setText("El espectador no tiene películas pendientes de votar.");
                return;
            }
            Espectador espectador = espectadores.get(comboEspectador.getSelectedIndex());
            int puntaje = (Integer) comboPuntaje.getSelectedItem();
            try {
                ventana.getPeliculaServicio().registrarVotoPublico(espectador, titulo, puntaje);
                cargarVotos();
                mostrarPremioPublico();
                ventana.setEstado("Voto registrado: " + espectador.getNombre() + " → " + titulo);
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void mostrarPremioPublico() {
        List<Edicion> ediciones = ventana.getFestivalServicio().getEdiciones();
        if (ediciones.isEmpty()) {
            areaPremio.setText("No hay ediciones registradas.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Edicion edicion : ediciones) {
            sb.append("Edición ").append(edicion.getAnio())
                    .append(" — ").append(edicion.getCiudad()).append("\n");
            sb.append("─────────────────────────────\n");
            Pelicula ganadora = ventana.getPeliculaServicio().determinarGanadoraPublico(edicion);
            if (ganadora != null) {
                sb.append("  🏆 ").append(ganadora.getTitulo())
                        .append(" (promedio: ")
                        .append(String.format("%.2f", ganadora.calcularPromedioPublico()))
                        .append(" — ").append(ganadora.getVotosPublico().size()).append(" votos)\n");
            } else {
                sb.append("  Sin votos del público.\n");
            }
            sb.append("\n");
        }
        areaPremio.setText(sb.toString());
        areaPremio.setCaretPosition(0);
    }
}
