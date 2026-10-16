package ui.evaluaciones;

import modelo.*;
import servicios.SistemaFestival;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVotoPublico extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaFestival sistema;
    private final DefaultTableModel modeloVotos;
    private final JTextArea areaPremio;

    public PanelVotoPublico(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.sistema = ventana.getSistema();
        ComponentesUI.configurarPanel(this);

        modeloVotos = ComponentesUI.crearModeloTabla("Película", "Edición", "Votos", "Promedio (1-5)");
        areaPremio = ComponentesUI.crearAreaResultados();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(450);
        splitPane.setLeftComponent(ComponentesUI.crearPanelConBorde("Votos por película", ComponentesUI.crearTabla(modeloVotos)));
        splitPane.setRightComponent(ComponentesUI.crearPanelConBorde("Premio del Público por edición", areaPremio));

        add(ComponentesUI.crearTitulo("Voto del Público"), BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(ComponentesUI.crearPanelBotones(ComponentesUI.crearBoton("Registrar voto", this::registrarVoto)), BorderLayout.SOUTH);

        cargarVotos();
        mostrarPremioPublico();
    }

    private void cargarVotos() {
        modeloVotos.setRowCount(0);
        for (Pelicula pelicula : sistema.getPeliculaServicio().getPeliculas()) {
            modeloVotos.addRow(new Object[]{
                    pelicula.getTitulo(),
                    sistema.getFestivalServicio().buscarEdicionDePelicula(pelicula).map(Edicion::toString).orElse("-"),
                    pelicula.cantidadVotos(),
                    pelicula.tieneVotos() ? String.format("%.2f", pelicula.calcularPromedioPublico()) : "-"
            });
        }
    }

    private void registrarVoto() {
        List<Espectador> espectadores = sistema.getPersonaServicio().getEspectadores();
        List<Edicion> ediciones = sistema.getFestivalServicio().getEdiciones();
        if (espectadores.isEmpty()) {
            ComponentesUI.avisar(ventana, "No hay espectadores registrados.");
            return;
        }
        if (ediciones.isEmpty()) {
            ComponentesUI.avisar(ventana, "No hay ediciones registradas.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Registrar voto del público", 520, 330);
        formulario.setTextoAceptar("Votar");
        JComboBox<Edicion> comboEdicion = formulario.agregarCampo("Edición:", new JComboBox<>(ediciones.toArray(new Edicion[0])));
        JComboBox<String> comboEspectador = formulario.agregarCampo("Espectador:", new JComboBox<>(
                espectadores.stream().map(Espectador::getNombre).toArray(String[]::new)));
        JComboBox<String> comboPelicula = formulario.agregarCampo("Película:", new JComboBox<>());
        JComboBox<Integer> comboPuntaje = formulario.agregarCampo("Puntaje (estrellas):", new JComboBox<>(new Integer[]{5, 4, 3, 2, 1}));
        comboEdicion.setSelectedIndex(ediciones.size() - 1); // la edición más reciente

        JLabel labelAyuda = new JLabel();
        labelAyuda.setFont(ComponentesUI.FUENTE_AYUDA);
        labelAyuda.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
        formulario.agregarDebajo(labelAyuda);

        // Qué películas puede votar lo decide el servicio (antes este recorrido estaba en la UI)
        Runnable actualizarPeliculas = () -> {
            Edicion edicion = (Edicion) comboEdicion.getSelectedItem();
            Espectador espectador = espectadores.get(comboEspectador.getSelectedIndex());
            List<Pelicula> votables = sistema.getEvaluacionServicio().peliculasParaVotar(espectador, edicion);
            comboPelicula.removeAllItems();
            votables.forEach(pelicula -> comboPelicula.addItem(pelicula.getTitulo()));
            if (votables.isEmpty()) {
                labelAyuda.setForeground(ComponentesUI.COLOR_AVISO);
                labelAyuda.setText("<html>" + espectador.getNombre() + " no tiene películas para votar en la edición "
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

        formulario.alAceptar(() -> {
            String titulo = (String) comboPelicula.getSelectedItem();
            if (titulo == null) {
                formulario.mostrarError("No hay película para votar con esta edición y espectador.");
                return;
            }
            String nombreEspectador = (String) comboEspectador.getSelectedItem();
            sistema.getEvaluacionServicio().registrarVotoPublico(nombreEspectador, titulo, (Integer) comboPuntaje.getSelectedItem());
            cargarVotos();
            mostrarPremioPublico();
            ventana.setEstado("Voto registrado: " + nombreEspectador + " → " + titulo);
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    private void mostrarPremioPublico() {
        List<Edicion> ediciones = sistema.getFestivalServicio().getEdiciones();
        if (ediciones.isEmpty()) {
            areaPremio.setText("No hay ediciones registradas.");
            return;
        }

        StringBuilder texto = new StringBuilder();
        for (Edicion edicion : ediciones) {
            texto.append("Edición ").append(edicion.getAnio()).append(" — ").append(edicion.getCiudad()).append("\n");
            texto.append("─────────────────────────────\n");
            texto.append(edicion.determinarGanadoraPublico()
                    .map(ganadora -> String.format("  🏆 %s (promedio: %.2f — %d votos)%n",
                            ganadora.getTitulo(), ganadora.calcularPromedioPublico(), ganadora.cantidadVotos()))
                    .orElse("  Sin votos del público.\n"));
            texto.append("\n");
        }
        areaPremio.setText(texto.toString());
        areaPremio.setCaretPosition(0);
    }
}
