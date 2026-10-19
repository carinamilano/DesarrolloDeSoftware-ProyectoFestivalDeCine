package ui.evaluaciones;

import modelo.festival.Edicion;
import modelo.festival.Seccion;
import modelo.peliculas.Evaluacion;
import modelo.peliculas.Pelicula;
import modelo.personas.Jurado;
import servicios.EvaluacionServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;
import ui.comun.SelectorPersona;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelEvaluaciones extends JPanel {
    private final VentanaPrincipal ventana;
    private final EvaluacionServicio evaluacionServicio;
    private final PeliculaServicio peliculaServicio;
    private final PersonaServicio personaServicio;
    private final FestivalServicio festivalServicio;
    private final DefaultTableModel modeloEvaluaciones;
    private final JTextArea areaGanadores;

    public PanelEvaluaciones(VentanaPrincipal ventana, EvaluacionServicio evaluacionServicio, PeliculaServicio peliculaServicio,
                             PersonaServicio personaServicio, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.evaluacionServicio = evaluacionServicio;
        this.peliculaServicio = peliculaServicio;
        this.personaServicio = personaServicio;
        this.festivalServicio = festivalServicio;
        ComponentesUI.configurarPanel(this);

        modeloEvaluaciones = ComponentesUI.crearModeloTabla("Película", "Jurado", "Puntaje");
        areaGanadores = ComponentesUI.crearAreaResultados();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(450);
        splitPane.setLeftComponent(ComponentesUI.crearPanelConBorde("Evaluaciones registradas",
                ComponentesUI.crearTabla(modeloEvaluaciones)));
        splitPane.setRightComponent(ComponentesUI.crearPanelConBorde("Ganadores por sección", areaGanadores));

        add(ComponentesUI.crearTitulo("Evaluaciones y Ganadores"), BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(ComponentesUI.crearPanelBotones(
                ComponentesUI.crearBoton("Agregar evaluación", this::agregarEvaluacion)), BorderLayout.SOUTH);

        cargarEvaluaciones();
        mostrarGanadores();
    }

    private void cargarEvaluaciones() {
        modeloEvaluaciones.setRowCount(0);
        for (Evaluacion evaluacion : evaluacionServicio.getEvaluaciones()) {
            modeloEvaluaciones.addRow(new Object[]{
                    evaluacion.getTituloPelicula(),
                    evaluacion.getNombreJurado(),
                    String.format("%.1f", evaluacion.getPuntaje())
            });
        }
    }

    private void agregarEvaluacion() {
        List<Pelicula> peliculas = peliculaServicio.getPeliculas();
        if (peliculas.isEmpty()) {
            ComponentesUI.avisar(ventana, "Se necesitan películas registradas.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Agregar evaluación", 500, 260);
        JComboBox<Pelicula> comboPelicula = formulario.agregarCampo("Película:",
                ComponentesUI.crearCombo(peliculas, Pelicula::getTitulo));
        // Jurados existentes y un botón "Nuevo..." (antes había una opción "-- Nuevo jurado --" mezclada con los nombres)
        JComboBox<Jurado> comboJurado = SelectorPersona.crearCombo(personaServicio.getJurados());
        formulario.agregarCampo("Jurado:", SelectorPersona.conBotonNuevo(comboJurado, "Dar de alta un jurado nuevo",
                () -> DialogoPersona.mostrar(formulario.getDialogo(), "Nuevo jurado", personaServicio::registrarJurado)));
        JTextField campoPuntaje = formulario.agregarCampo("Puntaje (0-10):", new JTextField());
        formulario.encadenarEnter(campoPuntaje);

        // Que un jurado evalúe una película una sola vez lo controla Pelicula
        formulario.alAceptar(() -> {
            Jurado jurado = ComponentesUI.seleccionado(comboJurado);
            if (jurado == null) {
                formulario.mostrarError("Elija un jurado o dé de alta uno nuevo.");
                return;
            }
            double puntaje = LectorCampos.decimal(campoPuntaje,
                    "El puntaje es obligatorio.", "El puntaje debe ser un número. Ej: 8.5");
            Pelicula pelicula = ComponentesUI.seleccionado(comboPelicula);
            evaluacionServicio.registrarEvaluacion(jurado, pelicula, puntaje);
            cargarEvaluaciones();
            mostrarGanadores();
            ventana.setEstado("Evaluación agregada para: " + pelicula.getTitulo());
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    private void mostrarGanadores() {
        List<Edicion> ediciones = festivalServicio.getEdiciones();
        if (ediciones.isEmpty()) {
            areaGanadores.setText("No hay ediciones registradas.");
            return;
        }

        StringBuilder texto = new StringBuilder();
        for (Edicion edicion : ediciones) {
            texto.append("Edición ").append(edicion.getAnio()).append(" — ").append(edicion.getCiudad()).append("\n");
            texto.append("─────────────────────────────\n");
            if (edicion.getSecciones().isEmpty()) {
                texto.append("  Sin secciones registradas.\n");
            }
            for (Seccion seccion : edicion.getSecciones()) {
                texto.append("  ").append(seccion.getTipo()).append(":\n");
                texto.append(seccion.determinarGanadora()
                        .map(ganadora -> String.format("    🏆 %s (promedio: %.2f)%n",
                                ganadora.getTitulo(), ganadora.calcularPromedioJurado()))
                        .orElse("    Sin películas evaluadas.\n"));
            }
            texto.append("\n");
        }
        areaGanadores.setText(texto.toString());
        areaGanadores.setCaretPosition(0);
        ventana.setEstado("Ganadores calculados correctamente.");
    }
}
