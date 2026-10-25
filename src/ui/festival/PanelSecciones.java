package ui.festival;

import excepciones.FestivalException;
import modelo.*;
import servicios.FestivalServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/* CORRECCION S8: el diálogo de alta de sección pasó a DialogoAgregarSeccion y el filtro de
películas asociables a FestivalServicio, así este panel solo muestra y llama al servicio.*/
public class PanelSecciones extends JPanel {

    private VentanaPrincipal ventana;
    private FestivalServicio festivalServicio;
    private JComboBox<Edicion> comboEdicion;
    private JTable tablaSecciones;
    private DefaultTableModel modeloSecciones;
    private JTable tablaPeliculas;
    private DefaultTableModel modeloPeliculas;
    private JButton btnAgregar;

    public PanelSecciones(VentanaPrincipal ventana, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.festivalServicio = festivalServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirPanelCentral();
        construirPanelBotones();

        cargarSecciones();
        actualizarBotonAgregar();
    }

    private void construirPanelSuperior() {
        // CORRECCION S10: el combo guarda las ediciones, no sus años.
        comboEdicion = ComponentesUI.crearComboEdiciones(festivalServicio.getEdiciones());
        comboEdicion.addActionListener(e -> {
            cargarSecciones();
            actualizarBotonAgregar();
        });
        JPanel panelEdicion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelEdicion.add(new JLabel("Edición:"));
        panelEdicion.add(comboEdicion);

        add(ComponentesUI.crearEncabezado("Gestión de Secciones", panelEdicion), BorderLayout.NORTH);
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        modeloSecciones = ComponentesUI.crearModeloTabla("Sección", "Premio", "Descripción", "Películas", "Ganadora (jurado)");
        tablaSecciones = ComponentesUI.crearTabla(modeloSecciones);
        tablaSecciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarPeliculas();
        });

        JPanel panelSecciones = new JPanel(new BorderLayout());
        panelSecciones.setBorder(BorderFactory.createTitledBorder("Secciones de la edición"));
        panelSecciones.add(new JScrollPane(tablaSecciones), BorderLayout.CENTER);

        modeloPeliculas = ComponentesUI.crearModeloTabla("Título", "Género", "Director", "Promedio jurado");
        tablaPeliculas = ComponentesUI.crearTabla(modeloPeliculas);

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
        return ComponentesUI.seleccionado(comboEdicion);
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
            // CORRECCION I12: solo hay ganadora si alguna película de la sección fue evaluada.
            modeloSecciones.addRow(new Object[]{
                    s.getTipo(),
                    s.getPremio().getNombre(),
                    s.getPremio().getDescripcion(),
                    s.getPeliculas().size(),
                    s.tieneGanadora() ? s.determinarGanadora().getTitulo() : "-"
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
                    String.format("%.2f", p.getCalificaciones().calcularPromedio())
            });
        }
    }

    private void actualizarBotonAgregar() {
        Edicion edicion = getEdicionSeleccionada();
        boolean completa = edicion != null && edicion.getTiposSinSeccion().isEmpty();
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

    private void agregarSeccion() {
        Edicion edicion = getEdicionSeleccionada();
        if (edicion == null) {
            ComponentesUI.mostrarAviso(ventana, "No hay ediciones registradas. Registre una desde Festival > Ediciones.");
            return;
        }
        if (edicion.getTiposSinSeccion().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "La edición " + edicion.getAnio() + " ya tiene todas las secciones.");
            return;
        }

        DialogoAgregarSeccion dialogo = new DialogoAgregarSeccion(ventana, festivalServicio, edicion);
        if (dialogo.mostrar()) {
            TipoSeccion tipo = dialogo.getTipoAgregado();
            cargarSecciones();
            actualizarBotonAgregar();
            seleccionarSeccion(tipo);
            ventana.setEstado("Sección agregada: " + tipo + " (edición " + edicion.getAnio() + ")");
        }
    }

    private void asociarPelicula() {
        Edicion edicion = getEdicionSeleccionada();
        Seccion seccion = getSeccionSeleccionada();
        if (seccion == null) {
            ComponentesUI.mostrarAviso(ventana, "Seleccione una sección de la tabla.");
            return;
        }

        // CORRECCION S7: qué películas se pueden asociar lo decide FestivalServicio.
        List<Pelicula> asociables = festivalServicio.peliculasAsociables(edicion, seccion);
        if (asociables.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay películas disponibles: cada película compite en una sola edición\n"
                            + "y las que quedan ya están en esta sección o en otra edición.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<Pelicula> comboPelicula = ComponentesUI.crearComboPeliculas(asociables);
        int opcion = JOptionPane.showConfirmDialog(ventana, comboPelicula,
                "Película a asociar a " + seccion.getTipo(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;

        Pelicula pelicula = ComponentesUI.seleccionado(comboPelicula);
        TipoSeccion tipo = seccion.getTipo();
        try {
            festivalServicio.asociarPeliculaASeccion(edicion, tipo, pelicula);
            cargarSecciones();
            seleccionarSeccion(tipo);
            ventana.setEstado("Película " + pelicula.getTitulo() + " asociada a " + tipo);
        } catch (FestivalException ex) {
            ComponentesUI.mostrarError(ventana, ex.getMessage());
        } catch (RuntimeException ex) {
            ComponentesUI.mostrarError(ventana, ComponentesUI.errorInesperado(ex));
        }
    }

    private void quitarPelicula() {
        Edicion edicion = getEdicionSeleccionada();
        Seccion seccion = getSeccionSeleccionada();
        int fila = tablaPeliculas.getSelectedRow();
        if (seccion == null || fila < 0) {
            ComponentesUI.mostrarAviso(ventana, "Seleccione una sección y una película de la sección.");
            return;
        }
        Pelicula pelicula = seccion.getPeliculas().get(tablaPeliculas.convertRowIndexToModel(fila));

        int confirmacion = JOptionPane.showConfirmDialog(ventana,
                "¿Quitar \"" + pelicula.getTitulo() + "\" de la sección " + seccion.getTipo() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        TipoSeccion tipo = seccion.getTipo();
        try {
            // CORRECCION I7: si tiene funciones y es su única sección en la edición, el modelo lo rechaza.
            festivalServicio.quitarPeliculaDeSeccion(edicion, tipo, pelicula);
            cargarSecciones();
            seleccionarSeccion(tipo);
            ventana.setEstado("Película " + pelicula.getTitulo() + " quitada de " + tipo);
        } catch (FestivalException ex) {
            ComponentesUI.mostrarError(ventana, ex.getMessage());
        } catch (RuntimeException ex) {
            ComponentesUI.mostrarError(ventana, ComponentesUI.errorInesperado(ex));
        }
    }
}
