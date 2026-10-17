package ui.festival;

import excepciones.FestivalException;
import modelo.festival.Edicion;
import modelo.festival.Seccion;
import modelo.festival.TipoSeccion;
import modelo.peliculas.Pelicula;
import servicios.FestivalServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelSecciones extends JPanel {
    private static final int COLUMNA_TIPO = 0;

    private final VentanaPrincipal ventana;
    private final FestivalServicio festivalServicio;
    private final JComboBox<Edicion> comboEdicion;
    private final DefaultTableModel modeloSecciones;
    private final JTable tablaSecciones;
    private final DefaultTableModel modeloPeliculas;
    private final JTable tablaPeliculas;
    private final JButton botonAgregar;
    private List<Pelicula> peliculasMostradas = List.of();

    public PanelSecciones(VentanaPrincipal ventana, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.festivalServicio = festivalServicio;
        ComponentesUI.configurarPanel(this);

        comboEdicion = new JComboBox<>(festivalServicio.getEdiciones().toArray(new Edicion[0]));
        comboEdicion.addActionListener(e -> {
            cargarSecciones();
            actualizarBotonAgregar();
        });

        modeloSecciones = ComponentesUI.crearModeloTabla("Sección", "Premio", "Descripción", "Películas", "Ganadora (jurado)");
        tablaSecciones = ComponentesUI.crearTabla(modeloSecciones);
        tablaSecciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarPeliculas();
        });
        modeloPeliculas = ComponentesUI.crearModeloTabla("Título", "Género", "Director", "Promedio jurado");
        tablaPeliculas = ComponentesUI.crearTabla(modeloPeliculas);
        botonAgregar = ComponentesUI.crearBoton("Agregar sección", this::agregarSeccion);

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(ComponentesUI.crearPanelBotones(botonAgregar,
                ComponentesUI.crearBoton("Asociar película", this::asociarPelicula),
                ComponentesUI.crearBoton("Quitar película", this::quitarPelicula)), BorderLayout.SOUTH);

        cargarSecciones();
        actualizarBotonAgregar();
    }

    private JPanel crearEncabezado() {
        JPanel panelEdicion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelEdicion.add(new JLabel("Edición:"));
        panelEdicion.add(comboEdicion);

        JPanel encabezado = new JPanel(new BorderLayout(10, 0));
        encabezado.add(ComponentesUI.crearTitulo("Gestión de Secciones"), BorderLayout.WEST);
        encabezado.add(panelEdicion, BorderLayout.EAST);
        return encabezado;
    }

    private JSplitPane crearPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setTopComponent(ComponentesUI.crearPanelConBorde("Secciones de la edición", tablaSecciones));
        splitPane.setBottomComponent(ComponentesUI.crearPanelConBorde("Películas de la sección seleccionada", tablaPeliculas));
        return splitPane;
    }

    private Edicion getEdicionSeleccionada() {
        return ComponentesUI.seleccionado(comboEdicion);
    }

    private Seccion getSeccionSeleccionada() {
        Edicion edicion = getEdicionSeleccionada();
        int fila = tablaSecciones.getSelectedRow();
        if (edicion == null || fila < 0) return null;
        TipoSeccion tipo = (TipoSeccion) modeloSecciones.getValueAt(fila, COLUMNA_TIPO);
        return edicion.buscarSeccion(tipo).orElse(null);
    }

    private void cargarSecciones() {
        modeloSecciones.setRowCount(0);
        modeloPeliculas.setRowCount(0);
        Edicion edicion = getEdicionSeleccionada();
        if (edicion == null) return;
        for (Seccion seccion : edicion.getSecciones()) {
            modeloSecciones.addRow(new Object[]{
                    seccion.getTipo(),
                    seccion.getPremio().getNombre(),
                    seccion.getPremio().getDescripcion(),
                    seccion.cantidadPeliculas(),
                    seccion.determinarGanadora().map(Pelicula::getTitulo).orElse("-")
            });
        }
    }

    private void cargarPeliculas() {
        modeloPeliculas.setRowCount(0);
        Seccion seccion = getSeccionSeleccionada();
        peliculasMostradas = seccion == null ? List.of() : seccion.getPeliculas();
        for (Pelicula pelicula : peliculasMostradas) {
            modeloPeliculas.addRow(new Object[]{
                    pelicula.getTitulo(),
                    pelicula.getGenero(),
                    pelicula.getNombreDirector(),
                    String.format("%.2f", pelicula.calcularPromedioJurado())
            });
        }
    }

    // El botón solo se habilita si la edición elegida todavía no tiene todos los tipos de sección
    private void actualizarBotonAgregar() {
        Edicion edicion = getEdicionSeleccionada();
        boolean completa = edicion != null && edicion.tieneTodasLasSecciones();
        botonAgregar.setEnabled(edicion != null && !completa);
        if (edicion == null) {
            botonAgregar.setToolTipText("No hay ediciones registradas.");
        } else if (completa) {
            botonAgregar.setToolTipText("La edición " + edicion.getAnio() + " ya tiene todas las secciones.");
        } else {
            botonAgregar.setToolTipText("Agregar una sección a la edición " + edicion.getAnio());
        }
    }

    private void seleccionarSeccion(TipoSeccion tipo) {
        for (int i = 0; i < modeloSecciones.getRowCount(); i++) {
            if (modeloSecciones.getValueAt(i, COLUMNA_TIPO) == tipo) {
                tablaSecciones.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private void refrescarSeleccionando(TipoSeccion tipo) {
        cargarSecciones();
        actualizarBotonAgregar();
        seleccionarSeccion(tipo);
    }

    // ========== AGREGAR SECCIÓN ==========

    private void agregarSeccion() {
        Edicion edicion = getEdicionSeleccionada();
        if (edicion == null) {
            ComponentesUI.avisar(ventana, "No hay ediciones registradas. Registre una desde Festival > Ediciones.");
            return;
        }
        if (edicion.tieneTodasLasSecciones()) {
            ComponentesUI.informar(ventana, "La edición " + edicion.getAnio() + " ya tiene todas las secciones.");
            return;
        }
        new DialogoAgregarSeccion(ventana, festivalServicio, edicion).mostrar().ifPresent(seccion -> {
            refrescarSeleccionando(seccion.getTipo());
            ventana.setEstado("Sección agregada: " + seccion.getTipo() + " (edición " + edicion.getAnio() + ")");
        });
    }

    // ========== ASOCIAR PELÍCULA ==========

    private void asociarPelicula() {
        Edicion edicion = getEdicionSeleccionada();
        Seccion seccion = getSeccionSeleccionada();
        if (seccion == null) {
            ComponentesUI.avisar(ventana, "Seleccione una sección de la tabla.");
            return;
        }

        // Qué películas se pueden asociar lo decide el servicio
        Pelicula[] asociables = festivalServicio.peliculasAsociables(edicion, seccion).toArray(new Pelicula[0]);
        if (asociables.length == 0) {
            ComponentesUI.informar(ventana, "No hay películas disponibles: cada película compite en una sola edición\n"
                    + "y las que quedan ya están en esta sección o en otra edición.");
            return;
        }

        Pelicula pelicula = (Pelicula) JOptionPane.showInputDialog(ventana,
                "Película a asociar a " + seccion.getTipo() + ":", "Asociar película",
                JOptionPane.PLAIN_MESSAGE, null, asociables, asociables[0]);
        if (pelicula == null) return;

        try {
            festivalServicio.asociarPeliculaASeccion(edicion.getAnio(), seccion.getTipo(), pelicula);
            refrescarSeleccionando(seccion.getTipo());
            ventana.setEstado("Película " + pelicula.getTitulo() + " asociada a " + seccion.getTipo());
        } catch (FestivalException ex) {
            ComponentesUI.mostrarError(ventana, "Error", ex.getMessage());
        }
    }

    // ========== QUITAR PELÍCULA ==========

    private void quitarPelicula() {
        Edicion edicion = getEdicionSeleccionada();
        Seccion seccion = getSeccionSeleccionada();
        int fila = tablaPeliculas.getSelectedRow();
        if (seccion == null || fila < 0) {
            ComponentesUI.avisar(ventana, "Seleccione una sección y una película de la sección.");
            return;
        }
        Pelicula pelicula = peliculasMostradas.get(fila);
        if (!ComponentesUI.confirmar(ventana, "¿Quitar \"" + pelicula.getTitulo() + "\" de la sección " + seccion.getTipo() + "?", "Confirmar")) {
            return;
        }

        try {
            festivalServicio.quitarPeliculaDeSeccion(edicion.getAnio(), seccion.getTipo(), pelicula);
            refrescarSeleccionando(seccion.getTipo());
            ventana.setEstado("Película " + pelicula.getTitulo() + " quitada de " + seccion.getTipo());
        } catch (FestivalException ex) {
            ComponentesUI.mostrarError(ventana, "Error", ex.getMessage());
        }
    }
}
