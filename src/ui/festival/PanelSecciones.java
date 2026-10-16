package ui.festival;

import modelo.*;
import servicios.FestivalServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelSecciones extends JPanel {
    private static final int COLUMNA_TIPO = 0;
    private static final int COLUMNA_TITULO = 0;

    private final VentanaPrincipal ventana;
    private final FestivalServicio festivalServicio;
    private final JComboBox<Edicion> comboEdicion;
    private final DefaultTableModel modeloSecciones;
    private final JTable tablaSecciones;
    private final DefaultTableModel modeloPeliculas;
    private final JTable tablaPeliculas;
    private final JButton botonAgregar;

    public PanelSecciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.festivalServicio = ventana.getSistema().getFestivalServicio();
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
        return (Edicion) comboEdicion.getSelectedItem();
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
        if (seccion == null) return;
        for (Pelicula pelicula : seccion.getPeliculas()) {
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
        // Solo se ofrecen los tipos que la edición todavía no tiene
        List<TipoSeccion> disponibles = edicion.getTiposSinSeccion();
        if (disponibles.isEmpty()) {
            ComponentesUI.informar(ventana, "La edición " + edicion.getAnio() + " ya tiene todas las secciones.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Agregar sección - Edición " + edicion.getAnio(), 440, 250);
        JComboBox<TipoSeccion> comboTipo = formulario.agregarCampo("Tipo de sección:",
                new JComboBox<>(disponibles.toArray(new TipoSeccion[0])));
        JTextField campoPremio = formulario.agregarCampo("Nombre del premio:", new JTextField());
        JTextField campoDescripcion = formulario.agregarCampo("Descripción del premio:", new JTextField());
        formulario.encadenarEnter(campoPremio, campoDescripcion);

        formulario.alAceptar(() -> {
            TipoSeccion tipo = (TipoSeccion) comboTipo.getSelectedItem();
            festivalServicio.registrarSeccion(edicion.getAnio(), tipo,
                    campoPremio.getText(), campoDescripcion.getText());
            refrescarSeleccionando(tipo);
            ventana.setEstado("Sección agregada: " + tipo + " (edición " + edicion.getAnio() + ")");
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    // ========== ASOCIAR PELÍCULA ==========

    private void asociarPelicula() {
        Edicion edicion = getEdicionSeleccionada();
        Seccion seccion = getSeccionSeleccionada();
        if (seccion == null) {
            ComponentesUI.avisar(ventana, "Seleccione una sección de la tabla.");
            return;
        }

        // Qué películas se pueden asociar lo decide el servicio (antes este filtro estaba en la UI)
        String[] titulos = festivalServicio.peliculasAsociables(edicion, seccion).stream()
                .map(Pelicula::getTitulo).toArray(String[]::new);
        if (titulos.length == 0) {
            ComponentesUI.informar(ventana, "No hay películas disponibles: cada película compite en una sola edición\n"
                    + "y las que quedan ya están en esta sección o en otra edición.");
            return;
        }

        String titulo = (String) JOptionPane.showInputDialog(ventana,
                "Película a asociar a " + seccion.getTipo() + ":", "Asociar película",
                JOptionPane.PLAIN_MESSAGE, null, titulos, titulos[0]);
        if (titulo == null) return;

        try {
            Pelicula pelicula = ventana.getSistema().getPeliculaServicio().buscarPelicula(titulo);
            festivalServicio.asociarPeliculaASeccion(edicion.getAnio(), seccion.getTipo(), pelicula);
            refrescarSeleccionando(seccion.getTipo());
            ventana.setEstado("Película " + titulo + " asociada a " + seccion.getTipo());
        } catch (RuntimeException ex) {
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
        String titulo = (String) modeloPeliculas.getValueAt(fila, COLUMNA_TITULO);
        if (!ComponentesUI.confirmar(ventana, "¿Quitar \"" + titulo + "\" de la sección " + seccion.getTipo() + "?", "Confirmar")) {
            return;
        }

        try {
            Pelicula pelicula = ventana.getSistema().getPeliculaServicio().buscarPelicula(titulo);
            festivalServicio.quitarPeliculaDeSeccion(edicion.getAnio(), seccion.getTipo(), pelicula);
            refrescarSeleccionando(seccion.getTipo());
            ventana.setEstado("Película " + titulo + " quitada de " + seccion.getTipo());
        } catch (RuntimeException ex) {
            ComponentesUI.mostrarError(ventana, "Error", ex.getMessage());
        }
    }
}
