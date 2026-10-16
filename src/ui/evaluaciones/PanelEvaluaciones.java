package ui.evaluaciones;

import modelo.*;
import servicios.SistemaFestival;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PanelEvaluaciones extends JPanel {
    private static final String NUEVO_JURADO = "-- Nuevo jurado --";

    private final VentanaPrincipal ventana;
    private final SistemaFestival sistema;
    private final DefaultTableModel modeloEvaluaciones;
    private final JTextArea areaGanadores;

    public PanelEvaluaciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.sistema = ventana.getSistema();
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
        for (Evaluacion evaluacion : sistema.getEvaluacionServicio().getEvaluaciones()) {
            modeloEvaluaciones.addRow(new Object[]{
                    evaluacion.getTituloPelicula(),
                    evaluacion.getNombreJurado(),
                    String.format("%.1f", evaluacion.getPuntaje())
            });
        }
    }

    private void agregarEvaluacion() {
        List<Pelicula> peliculas = sistema.getPeliculaServicio().getPeliculas();
        if (peliculas.isEmpty()) {
            ComponentesUI.avisar(ventana, "Se necesitan películas registradas.");
            return;
        }
        // El combo de jurados tiene la opción "Nuevo jurado" al principio
        String[] opcionesJurado = Stream.concat(Stream.of(NUEVO_JURADO),
                sistema.getPersonaServicio().getJurados().stream().map(Jurado::getNombre)).toArray(String[]::new);

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Agregar evaluación", 400, 260);
        JComboBox<String> comboPelicula = formulario.agregarCampo("Película:", new JComboBox<>(
                peliculas.stream().map(Pelicula::getTitulo).toArray(String[]::new)));
        JComboBox<String> comboJurado = formulario.agregarCampo("Jurado:", new JComboBox<>(opcionesJurado));
        JTextField campoPuntaje = formulario.agregarCampo("Puntaje (0-10):", new JTextField());
        formulario.encadenarEnter(campoPuntaje);

        formulario.alAceptar(() -> {
            double puntaje = LectorCampos.decimal(campoPuntaje,
                    "El puntaje es obligatorio.", "El puntaje debe ser un número. Ej: 8.5");
            Optional<String> jurado = elegirJurado(formulario, (String) comboJurado.getSelectedItem());
            if (jurado.isEmpty()) return; // canceló el alta del nuevo jurado

            String tituloPelicula = (String) comboPelicula.getSelectedItem();
            sistema.getEvaluacionServicio().registrarEvaluacion(jurado.get(), tituloPelicula, puntaje);
            cargarEvaluaciones();
            mostrarGanadores();
            ventana.setEstado("Evaluación agregada para: " + tituloPelicula);
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    // Si se eligió "Nuevo jurado" se piden sus datos (mismo diálogo que el resto de las personas)
    private Optional<String> elegirJurado(FormularioDialogo formulario, String seleccion) {
        if (!NUEVO_JURADO.equals(seleccion)) return Optional.of(seleccion);
        return Optional.ofNullable(DialogoPersona.mostrar(formulario.getDialogo(), "Nuevo jurado",
                sistema.getPersonaServicio()::registrarJurado));
    }

    private void mostrarGanadores() {
        List<Edicion> ediciones = sistema.getFestivalServicio().getEdiciones();
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
