package ui.funciones;

import modelo.Funcion;
import modelo.Pelicula;
import modelo.Sala;
import servicios.SistemaFestival;
import ui.VentanaPrincipal;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PanelFunciones extends JPanel {
    private static final int OCUPACION_ALTA = 90;
    private static final int OCUPACION_MEDIA = 60;
    private static final Color COLOR_OCUPACION_ALTA = new Color(200, 50, 50);
    private static final Color COLOR_OCUPACION_MEDIA = new Color(220, 150, 0);
    private static final Color COLOR_OCUPACION_BAJA = new Color(50, 160, 50);

    private final VentanaPrincipal ventana;
    private final SistemaFestival sistema;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JProgressBar barraOcupacion;
    private List<Funcion> funcionesMostradas = List.of();

    public PanelFunciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.sistema = ventana.getSistema();
        ComponentesUI.configurarPanel(this);

        modeloTabla = ComponentesUI.crearModeloTabla("Película", "Tipo", "Fecha", "Horario", "Sala / Enlace", "Ocupación %");
        tabla = ComponentesUI.crearTabla(modeloTabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarBarraOcupacion();
        });
        barraOcupacion = new JProgressBar(0, 100);
        barraOcupacion.setStringPainted(true);
        barraOcupacion.setPreferredSize(new Dimension(0, 30));

        add(ComponentesUI.crearTitulo("Gestión de Funciones"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        cargarDatos();
    }

    private JPanel crearPanelInferior() {
        JPanel panelOcupacion = new JPanel(new BorderLayout(10, 0));
        panelOcupacion.setBorder(BorderFactory.createTitledBorder("Ocupación de la función seleccionada"));
        panelOcupacion.add(barraOcupacion, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 5));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panelInferior.add(panelOcupacion, BorderLayout.CENTER);
        panelInferior.add(ComponentesUI.crearPanelBotones(
                ComponentesUI.crearBoton("Agregar función presencial", this::agregarFuncionPresencial),
                ComponentesUI.crearBoton("Agregar función streaming", this::agregarFuncionStreaming)), BorderLayout.SOUTH);
        return panelInferior;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        funcionesMostradas = sistema.getFuncionServicio().getFunciones();
        // Polimorfismo: cada función sabe su modalidad y su lugar (antes había instanceof + casteos)
        for (Funcion funcion : funcionesMostradas) {
            modeloTabla.addRow(new Object[]{
                    funcion.getTituloPelicula(),
                    funcion.getModalidad(),
                    funcion.getFecha().toString(),
                    funcion.getHorario().toString(),
                    funcion.getLugar(),
                    String.format("%.1f%%", funcion.calcularOcupacion())
            });
        }
        barraOcupacion.setValue(0);
        barraOcupacion.setString("Seleccione una función");
    }

    private void actualizarBarraOcupacion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            barraOcupacion.setValue(0);
            barraOcupacion.setString("Seleccione una función");
            return;
        }
        Funcion funcion = funcionesMostradas.get(fila);
        int ocupacion = (int) funcion.calcularOcupacion();
        barraOcupacion.setValue(ocupacion);
        barraOcupacion.setString(ocupacion + "% ocupado — " + funcion.getTituloPelicula());
        barraOcupacion.setForeground(colorSegunOcupacion(ocupacion));
    }

    private Color colorSegunOcupacion(int ocupacion) {
        if (ocupacion >= OCUPACION_ALTA) return COLOR_OCUPACION_ALTA;
        if (ocupacion >= OCUPACION_MEDIA) return COLOR_OCUPACION_MEDIA;
        return COLOR_OCUPACION_BAJA;
    }

    private String[] titulosDePeliculas() {
        return sistema.getPeliculaServicio().getPeliculas().stream().map(Pelicula::getTitulo).toArray(String[]::new);
    }

    private LocalDateTime leerFechaHora(JTextField campoFecha, JTextField campoHorario) throws CampoInvalidoException {
        LocalDate fecha = LectorCampos.fecha(campoFecha, "La fecha es obligatoria.");
        LocalTime horario = LectorCampos.horario(campoHorario, "El horario es obligatorio.");
        return LocalDateTime.of(fecha, horario);
    }

    private void agregarFuncionPresencial() {
        List<Sala> salas = sistema.getFestivalServicio().getSalas();
        if (sistema.getPeliculaServicio().getPeliculas().isEmpty()) {
            ComponentesUI.avisar(ventana, "No hay películas registradas.");
            return;
        }
        if (salas.isEmpty()) {
            ComponentesUI.avisar(ventana, "No hay salas registradas.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Agregar función presencial", 420, 280);
        JComboBox<String> comboPelicula = formulario.agregarCampo("Película:", new JComboBox<>(titulosDePeliculas()));
        JComboBox<String> comboSala = formulario.agregarCampo("Sala:", new JComboBox<>(
                salas.stream().map(Sala::getNombre).toArray(String[]::new)));
        JTextField campoFecha = formulario.agregarCampo("Fecha (AAAA-MM-DD):", new JTextField());
        JTextField campoHorario = formulario.agregarCampo("Horario (HH:MM):", new JTextField());
        formulario.encadenarEnter(campoFecha, campoHorario);

        formulario.alAceptar(() -> {
            LocalDateTime fechaHora = leerFechaHora(campoFecha, campoHorario);
            String titulo = (String) comboPelicula.getSelectedItem();
            Pelicula pelicula = sistema.getPeliculaServicio().buscarPelicula(titulo);
            Sala sala = sistema.getFestivalServicio().buscarSala((String) comboSala.getSelectedItem());
            sistema.getFuncionServicio().programarFuncionPresencial(pelicula, sala, fechaHora);
            cargarDatos();
            ventana.setEstado("Función presencial agregada: " + titulo);
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    private void agregarFuncionStreaming() {
        if (sistema.getPeliculaServicio().getPeliculas().isEmpty()) {
            ComponentesUI.avisar(ventana, "No hay películas registradas.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Agregar función streaming", 420, 300);
        JComboBox<String> comboPelicula = formulario.agregarCampo("Película:", new JComboBox<>(titulosDePeliculas()));
        JTextField campoEnlace = formulario.agregarCampo("Enlace:", new JTextField());
        JTextField campoCapacidad = formulario.agregarCampo("Capacidad virtual (max 1000):", new JTextField());
        JTextField campoFecha = formulario.agregarCampo("Fecha (AAAA-MM-DD):", new JTextField());
        JTextField campoHorario = formulario.agregarCampo("Horario (HH:MM):", new JTextField());
        formulario.encadenarEnter(campoEnlace, campoCapacidad, campoFecha, campoHorario);

        // El rango de la capacidad y que la fecha no haya pasado lo validan FuncionStreaming y FuncionServicio
        formulario.alAceptar(() -> {
            String enlace = LectorCampos.texto(campoEnlace, "El enlace es obligatorio.");
            int capacidad = LectorCampos.entero(campoCapacidad,
                    "La capacidad es obligatoria.", "La capacidad debe ser un número entero.");
            LocalDateTime fechaHora = leerFechaHora(campoFecha, campoHorario);
            String titulo = (String) comboPelicula.getSelectedItem();
            Pelicula pelicula = sistema.getPeliculaServicio().buscarPelicula(titulo);
            sistema.getFuncionServicio().programarFuncionStreaming(pelicula, enlace, capacidad, fechaHora);
            cargarDatos();
            ventana.setEstado("Función streaming agregada: " + titulo);
            formulario.cerrar();
        });
        formulario.mostrar();
    }
}
