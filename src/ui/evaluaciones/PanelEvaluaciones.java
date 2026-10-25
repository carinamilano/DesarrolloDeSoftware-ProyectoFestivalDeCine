package ui.evaluaciones;

import excepciones.FestivalException;
import modelo.*;
import servicios.EvaluacionServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelEvaluaciones extends JPanel {

    private VentanaPrincipal ventana;
    private EvaluacionServicio evaluacionServicio;
    private PeliculaServicio peliculaServicio;
    private PersonaServicio personaServicio;
    private FestivalServicio festivalServicio;
    private DefaultTableModel modeloEvaluaciones;
    private JTextArea areaGanadores;

    public PanelEvaluaciones(VentanaPrincipal ventana, EvaluacionServicio evaluacionServicio, PeliculaServicio peliculaServicio, PersonaServicio personaServicio, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.evaluacionServicio = evaluacionServicio;
        this.peliculaServicio = peliculaServicio;
        this.personaServicio = personaServicio;
        this.festivalServicio = festivalServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(ComponentesUI.crearEncabezado("Evaluaciones y Ganadores", null), BorderLayout.NORTH);
        construirPanelCentral();
        construirPanelBotones();
        cargarEvaluaciones();
        mostrarGanadores();
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(450);

        modeloEvaluaciones = ComponentesUI.crearModeloTabla("Película", "Jurado", "Puntaje");
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Evaluaciones registradas"));
        panelTabla.add(new JScrollPane(ComponentesUI.crearTabla(modeloEvaluaciones)), BorderLayout.CENTER);

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
        for (Evaluacion ev : evaluacionServicio.getEvaluaciones()) {
            modeloEvaluaciones.addRow(new Object[]{
                    ev.getPelicula().getTitulo(),
                    ev.getJurado().getNombre(),
                    String.format("%.1f", ev.getPuntaje())
            });
        }
    }

    private void agregarEvaluacion() {
        if (peliculaServicio.getPeliculas().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "Se necesitan películas registradas.");
            return;
        }

        JDialog dialog = new JDialog(ventana, "Agregar evaluación", true);
        dialog.setSize(460, 260);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        /* CORRECCION I10/S10: los combos guardan los objetos, el jurado se muestra con su email.
        En lugar de la opción "-- Nuevo jurado --" dentro del combo, hay un botón "Nuevo...".*/
        JComboBox<Pelicula> comboPelicula = ComponentesUI.crearComboPeliculas(peliculaServicio.getPeliculas());
        JComboBox<Jurado> comboJurado = ComponentesUI.crearComboPersonas(personaServicio.getJurados());
        JTextField campoPuntaje = new JTextField();

        JButton btnNuevoJurado = new JButton("Nuevo...");
        btnNuevoJurado.setToolTipText("Dar de alta un jurado nuevo");
        btnNuevoJurado.addActionListener(e -> {
            Jurado nuevo = darDeAltaJurado(dialog);
            if (nuevo != null) {
                comboJurado.addItem(nuevo);
                comboJurado.setSelectedItem(nuevo);
            }
        });
        JPanel panelJurado = new JPanel(new BorderLayout(5, 0));
        panelJurado.add(comboJurado, BorderLayout.CENTER);
        panelJurado.add(btnNuevoJurado, BorderLayout.EAST);

        JPanel panelCampos = ComponentesUI.crearPanelCampos(3);
        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Jurado:"));
        panelCampos.add(panelJurado);
        panelCampos.add(new JLabel("Puntaje (0-10):"));
        panelCampos.add(campoPuntaje);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoPuntaje.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        // CORRECCION I3/I9: si el jurado ya evaluó la película o el puntaje está fuera de rango, el modelo lo rechaza.
        btnAceptar.addActionListener(e -> {
            Jurado jurado = ComponentesUI.seleccionado(comboJurado);
            if (jurado == null) {
                labelError.setText("Seleccione un jurado o dé de alta uno nuevo.");
                return;
            }
            try {
                double puntaje = LectorCampos.decimal(campoPuntaje, "Puntaje");
                Pelicula pelicula = ComponentesUI.seleccionado(comboPelicula);
                evaluacionServicio.registrarEvaluacion(jurado, pelicula, puntaje);
                cargarEvaluaciones();
                mostrarGanadores();
                ventana.setEstado("Evaluación agregada para: " + pelicula.getTitulo());
                dialog.dispose();
            } catch (CampoInvalidoException ex) {
                labelError.setText(ex.getMessage());
                ex.getCampo().requestFocus();
            } catch (FestivalException ex) {
                labelError.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                labelError.setText(ComponentesUI.errorInesperado(ex));
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(ComponentesUI.crearPanelSur(labelError, btnAceptar, btnCancelar), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // CORRECCION S8: usa el formulario común DialogoPersona. Devuelve el jurado creado, o null si se canceló.
    private Jurado darDeAltaJurado(Window padre) {
        DialogoPersona dialogo = new DialogoPersona(padre, "Nuevo jurado");
        Jurado registrado = null;
        while (registrado == null && dialogo.mostrar()) {
            try {
                registrado = personaServicio.registrarJurado(dialogo.getNombre(), dialogo.getEmail(), dialogo.getNacionalidad());
            } catch (FestivalException ex) {
                dialogo.mostrarError(ex.getMessage());
            } catch (RuntimeException ex) {
                dialogo.mostrarError(ComponentesUI.errorInesperado(ex));
            }
        }
        dialogo.dispose();
        return registrado;
    }

    private void mostrarGanadores() {
        List<Edicion> ediciones = festivalServicio.getEdiciones();

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
                    // CORRECCION I12: antes podía mostrar como ganadora una película sin evaluaciones.
                    if (seccion.tieneGanadora()) {
                        Pelicula ganadora = seccion.determinarGanadora();
                        sb.append("    🏆 ").append(ganadora.getTitulo())
                                .append(" (promedio: ")
                                .append(String.format("%.2f", ganadora.getCalificaciones().calcularPromedio()))
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
