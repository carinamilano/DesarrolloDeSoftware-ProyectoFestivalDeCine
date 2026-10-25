package ui.evaluaciones;

import excepciones.FestivalException;
import modelo.*;
import servicios.EvaluacionServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVotoPublico extends JPanel {

    private VentanaPrincipal ventana;
    private EvaluacionServicio evaluacionServicio;
    private PeliculaServicio peliculaServicio;
    private PersonaServicio personaServicio;
    private FestivalServicio festivalServicio;
    private DefaultTableModel modeloVotos;
    private JTextArea areaPremio;

    public PanelVotoPublico(VentanaPrincipal ventana, EvaluacionServicio evaluacionServicio, PeliculaServicio peliculaServicio, PersonaServicio personaServicio, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.evaluacionServicio = evaluacionServicio;
        this.peliculaServicio = peliculaServicio;
        this.personaServicio = personaServicio;
        this.festivalServicio = festivalServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(ComponentesUI.crearEncabezado("Voto del Público", null), BorderLayout.NORTH);
        construirPanelCentral();
        construirPanelBotones();

        cargarVotos();
        mostrarPremioPublico();
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(450);
        
        modeloVotos = ComponentesUI.crearModeloTabla("Película", "Edición", "Votos", "Promedio (1-5)");
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Votos por película"));
        panelTabla.add(new JScrollPane(ComponentesUI.crearTabla(modeloVotos)), BorderLayout.CENTER);
        
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
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            int cantidad = p.getVotosPublico().size();
            // CORRECCION S5: se pregunta si compite antes de buscar la edición (antes se comparaba con null).
            String edicion = festivalServicio.compiteEnAlgunaEdicion(p)
                    ? ComponentesUI.describir(festivalServicio.buscarEdicionDe(p)) : "-";
            modeloVotos.addRow(new Object[]{
                    p.getTitulo(),
                    edicion,
                    cantidad,
                    cantidad == 0 ? "-" : String.format("%.2f", p.calcularPromedioPublico())
            });
        }
    }

    private void registrarVoto() {
        List<Espectador> espectadores = personaServicio.getEspectadores();
        if (espectadores.isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay espectadores registrados.");
            return;
        }

        List<Edicion> ediciones = festivalServicio.getEdiciones();
        if (ediciones.isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay ediciones registradas.");
            return;
        }

        JDialog dialog = new JDialog(ventana, "Registrar voto del público", true);
        dialog.setSize(560, 330);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        // CORRECCION S10/I10: los combos guardan los objetos (antes se elegía por posición o por nombre).
        JComboBox<Edicion> comboEdicion = ComponentesUI.crearComboEdiciones(ediciones);
        JComboBox<Espectador> comboEspectador = ComponentesUI.crearComboPersonas(espectadores);
        JComboBox<Pelicula> comboPelicula = ComponentesUI.crearComboPeliculas(List.of());
        JComboBox<Integer> comboPuntaje = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        comboEdicion.setSelectedIndex(ediciones.size() - 1); // la edición más reciente

        JPanel panelCampos = ComponentesUI.crearPanelCampos(4);
        panelCampos.add(new JLabel("Edición:"));
        panelCampos.add(comboEdicion);
        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(comboEspectador);
        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Puntaje (estrellas):"));
        panelCampos.add(comboPuntaje);

        JLabel labelAyuda = new JLabel();
        labelAyuda.setFont(new Font("SansSerif", Font.ITALIC, 12));
        labelAyuda.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));

        // CORRECCION S7: que películas puede votar el espectador lo decide EvaluacionServicio.
        Runnable actualizarPeliculas = () -> {
            comboPelicula.removeAllItems();
            Edicion edicion = ComponentesUI.seleccionado(comboEdicion);
            Espectador esp = ComponentesUI.seleccionado(comboEspectador);
            List<Pelicula> paraVotar = evaluacionServicio.peliculasParaVotar(esp, edicion);
            for (Pelicula p : paraVotar) comboPelicula.addItem(p);
            if (paraVotar.isEmpty()) {
                labelAyuda.setForeground(new Color(150, 50, 50));
                labelAyuda.setText("<html>" + esp.getNombre() + " no tiene películas para votar en la edición "
                        + edicion.getAnio() + ": solo se vota lo que se vio (con entrada) y una vez por película.</html>");
            } else {
                labelAyuda.setForeground(Color.DARK_GRAY);
                labelAyuda.setText("<html>Solo aparecen las películas de la edición para las que el espectador "
                        + "tiene entrada y todavía no votó.</html>");
            }
        };
        comboEdicion.addActionListener(e -> actualizarPeliculas.run());
        comboEspectador.addActionListener(e -> actualizarPeliculas.run());
        actualizarPeliculas.run();

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelCampos, BorderLayout.CENTER);
        panelCentro.add(labelAyuda, BorderLayout.SOUTH);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Votar");
        JButton btnCancelar = new JButton("Cancelar");

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            Pelicula pelicula = ComponentesUI.seleccionado(comboPelicula);
            if (pelicula == null) {
                labelError.setText("No hay película para votar con esta edición y espectador.");
                return;
            }
            Espectador espectador = ComponentesUI.seleccionado(comboEspectador);
            int puntaje = ComponentesUI.seleccionado(comboPuntaje);
            try {
                evaluacionServicio.registrarVotoPublico(espectador, pelicula, puntaje);
                cargarVotos();
                mostrarPremioPublico();
                ventana.setEstado("Voto registrado: " + espectador.getNombre() + " → " + pelicula.getTitulo());
                dialog.dispose();
            } catch (FestivalException ex) {
                labelError.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                labelError.setText(ComponentesUI.errorInesperado(ex));
            }
        });

        dialog.add(panelCentro, BorderLayout.CENTER);
        dialog.add(ComponentesUI.crearPanelSur(labelError, btnAceptar, btnCancelar), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void mostrarPremioPublico() {
        List<Edicion> ediciones = festivalServicio.getEdiciones();
        if (ediciones.isEmpty()) {
            areaPremio.setText("No hay ediciones registradas.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Edicion edicion : ediciones) {
            sb.append("Edición ").append(edicion.getAnio())
                    .append(" — ").append(edicion.getCiudad()).append("\n");
            sb.append("─────────────────────────────\n");
            // CORRECCION I12: con empate gana la película con más votos.
            if (edicion.tieneGanadoraPublico()) {
                Pelicula ganadora = edicion.determinarGanadoraPublico();
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
