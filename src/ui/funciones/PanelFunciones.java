package ui.funciones;

import modelo.Funcion;
import servicios.FestivalServicio;
import servicios.FuncionServicio;
import servicios.PeliculaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/* CORRECCION S8: los diálogos de alta pasaron a DialogoFuncionPresencial y DialogoFuncionStreaming;
el panel bajó de 508 a 166 líneas.*/
public class PanelFunciones extends JPanel {

    private VentanaPrincipal ventana;
    private FuncionServicio funcionServicio;
    private FestivalServicio festivalServicio;
    private PeliculaServicio peliculaServicio;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JProgressBar progressBar;

    public PanelFunciones(VentanaPrincipal ventana, FuncionServicio funcionServicio, FestivalServicio festivalServicio, PeliculaServicio peliculaServicio) {
        this.ventana = ventana;
        this.funcionServicio = funcionServicio;
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(ComponentesUI.crearEncabezado("Gestión de Funciones", null), BorderLayout.NORTH);
        construirTabla();
        construirPanelInferior();
        cargarDatos();
    }

    private void construirTabla() {
        // CORRECCION I11: se muestra el código de cada función.
        modeloTabla = ComponentesUI.crearModeloTabla("Código", "Película", "Tipo", "Fecha", "Horario", "Sala / Enlace", "Ocupación %");
        tabla = ComponentesUI.crearTabla(modeloTabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarProgressBar();
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void construirPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout(10, 5));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel panelOcupacion = new JPanel(new BorderLayout(10, 0));
        panelOcupacion.setBorder(BorderFactory.createTitledBorder("Ocupación de la función seleccionada"));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Seleccione una función");
        progressBar.setPreferredSize(new Dimension(0, 30));

        panelOcupacion.add(progressBar, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregarPresencial = new JButton("Agregar función presencial");
        btnAgregarPresencial.addActionListener(e -> agregarFuncionPresencial());

        JButton btnAgregarStreaming = new JButton("Agregar función streaming");
        btnAgregarStreaming.addActionListener(e -> agregarFuncionStreaming());

        panelBotones.add(btnAgregarPresencial);
        panelBotones.add(btnAgregarStreaming);

        panelInferior.add(panelOcupacion, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Funcion f : funcionServicio.getFunciones()) {
            // CORRECCION S3: el tipo y el lugar los responde cada función, sin instanceof.
            modeloTabla.addRow(new Object[]{
                    f.getCodigo(),
                    f.getPelicula().getTitulo(),
                    f.getTipo(),
                    f.getFecha().toString(),
                    f.getHorario().toString(),
                    f.getLugar(),
                    String.format("%.1f%%", f.calcularOcupacion())
            });
        }

        progressBar.setValue(0);
        progressBar.setString("Seleccione una función");
    }

    private void actualizarProgressBar() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            progressBar.setValue(0);
            progressBar.setString("Seleccione una función");
            return;
        }

        // CORRECCION S10: la función se busca por el código de la fila, no por su posición en la lista.
        String codigo = (String) modeloTabla.getValueAt(tabla.convertRowIndexToModel(filaSeleccionada), 0);
        Funcion funcion = funcionServicio.buscarFuncion(codigo);
        int ocupacion = (int) funcion.calcularOcupacion();

        progressBar.setValue(ocupacion);
        progressBar.setString(ocupacion + "% ocupado — " + funcion.getPelicula().getTitulo());

        if (ocupacion >= 90) {
            progressBar.setForeground(new Color(200, 50, 50));
        } else if (ocupacion >= 60) {
            progressBar.setForeground(new Color(220, 150, 0));
        } else {
            progressBar.setForeground(new Color(50, 160, 50));
        }
    }

    private void agregarFuncionPresencial() {
        if (!hayPeliculasProgramables()) return;
        if (festivalServicio.getSalas().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay salas registradas.");
            return;
        }
        DialogoFuncionPresencial dialogo = new DialogoFuncionPresencial(ventana, funcionServicio, festivalServicio);
        if (dialogo.mostrar()) {
            cargarDatos();
            ventana.setEstado("Función presencial agregada: " + dialogo.getFuncionCreada().getCodigo()
                    + " - " + dialogo.getFuncionCreada().getPelicula().getTitulo());
        }
    }

    private void agregarFuncionStreaming() {
        if (!hayPeliculasProgramables()) return;
        DialogoFuncionStreaming dialogo = new DialogoFuncionStreaming(ventana, funcionServicio, festivalServicio);
        if (dialogo.mostrar()) {
            cargarDatos();
            ventana.setEstado("Función streaming agregada: " + dialogo.getFuncionCreada().getCodigo()
                    + " - " + dialogo.getFuncionCreada().getPelicula().getTitulo());
        }
    }

    // CORRECCION I6: solo se programan funciones de películas que compiten en alguna edición.
    private boolean hayPeliculasProgramables() {
        if (peliculaServicio.getPeliculas().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay películas registradas.");
            return false;
        }
        if (festivalServicio.peliculasQueCompiten().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "Ninguna película compite en una edición. "
                    + "Asocie películas a una sección desde Festival > Secciones.");
            return false;
        }
        return true;
    }
}
