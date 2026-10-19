package ui.funciones;

import modelo.festival.Sala;
import modelo.funciones.Funcion;
import servicios.FestivalServicio;
import servicios.FuncionServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelFunciones extends JPanel {
    private static final int OCUPACION_ALTA = 90;
    private static final int OCUPACION_MEDIA = 60;
    private static final Color COLOR_OCUPACION_ALTA = new Color(200, 50, 50);
    private static final Color COLOR_OCUPACION_MEDIA = new Color(220, 150, 0);
    private static final Color COLOR_OCUPACION_BAJA = new Color(50, 160, 50);

    private final VentanaPrincipal ventana;
    private final FuncionServicio funcionServicio;
    private final FestivalServicio festivalServicio;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JProgressBar barraOcupacion;
    private List<Funcion> funcionesMostradas = List.of();

    public PanelFunciones(VentanaPrincipal ventana, FuncionServicio funcionServicio, FestivalServicio festivalServicio) {
        this.ventana = ventana;
        this.funcionServicio = funcionServicio;
        this.festivalServicio = festivalServicio;
        ComponentesUI.configurarPanel(this);

        modeloTabla = ComponentesUI.crearModeloTabla("Película", "Tipo", "Fecha", "Inicio", "Fin", "Sala / Enlace", "Ocupación %");
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
        funcionesMostradas = funcionServicio.getFunciones();
        // Polimorfismo: cada función sabe su modalidad y su lugar
        for (Funcion funcion : funcionesMostradas) {
            modeloTabla.addRow(new Object[]{
                    funcion.getTituloPelicula(),
                    funcion.getModalidad(),
                    funcion.getFecha().toString(),
                    funcion.getHorario().toString(),
                    funcion.getFin().toLocalTime().toString(),
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

    private void agregarFuncionPresencial() {
        List<Sala> salas = festivalServicio.getSalas();
        if (salas.isEmpty()) {
            ComponentesUI.avisar(ventana, "No hay salas registradas.");
            return;
        }
        abrir(new DialogoFuncionPresencial(ventana, funcionServicio, salas));
    }

    private void agregarFuncionStreaming() {
        abrir(new DialogoFuncionStreaming(ventana, funcionServicio));
    }

    private void abrir(DialogoFuncion dialogo) {
        if (!dialogo.hayPeliculasProgramables()) {
            ComponentesUI.avisar(ventana, "No hay películas para programar: una función solo se puede programar\n"
                    + "para una película que compite en una edición que todavía no terminó.");
            return;
        }
        dialogo.mostrar().ifPresent(funcion -> {
            cargarDatos();
            ventana.setEstado("Función " + funcion.getModalidad().toLowerCase() + " agregada: " + funcion.getTituloPelicula());
        });
    }
}
