package ui.festival;

import servicios.FestivalServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;
import ui.comun.PanelListado;

import javax.swing.JTextField;
import java.time.LocalDate;
import java.util.List;

public class PanelEdiciones extends PanelListado {
    private final FestivalServicio festivalServicio;

    public PanelEdiciones(VentanaPrincipal ventana, FestivalServicio festivalServicio) {
        super(ventana, "Gestión de Ediciones", "Año", "Ciudad", "Fecha inicio (AAAA-MM-DD)", "Fecha fin (AAAA-MM-DD)");
        this.festivalServicio = festivalServicio;
        agregarBotones(ComponentesUI.crearBoton("Agregar edición", this::agregarEdicion));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return festivalServicio.getEdiciones().stream()
                .map(edicion -> new Object[]{
                        edicion.getAnio(), edicion.getCiudad(),
                        edicion.getFechaInicio().toString(), edicion.getFechaFin().toString()})
                .toList();
    }

    private void agregarEdicion() {
        FormularioDialogo formulario = new FormularioDialogo(getVentana(), "Agregar edición", 420, 320);
        JTextField campoAnio = formulario.agregarCampo("Año (2001-2030):", new JTextField());
        JTextField campoCiudad = formulario.agregarCampo("Ciudad:", new JTextField());
        JTextField campoFechaInicio = formulario.agregarCampo("Fecha inicio (AAAA-MM-DD):", new JTextField());
        JTextField campoFechaFin = formulario.agregarCampo("Fecha fin (AAAA-MM-DD):", new JTextField());
        formulario.encadenarEnter(campoAnio, campoCiudad, campoFechaInicio, campoFechaFin);

        // La UI solo lee los datos; el rango del año y la coherencia de las fechas los valida Edicion
        formulario.alAceptar(() -> {
            int anio = LectorCampos.entero(campoAnio, "El año es obligatorio.", "El año debe ser un número entero.");
            String ciudad = LectorCampos.texto(campoCiudad, "La ciudad es obligatoria.");
            LocalDate fechaInicio = LectorCampos.fecha(campoFechaInicio, "La fecha de inicio es obligatoria.");
            LocalDate fechaFin = LectorCampos.fecha(campoFechaFin, "La fecha fin es obligatoria.");
            festivalServicio.registrarEdicion(anio, ciudad, fechaInicio, fechaFin);
            recargar();
            getVentana().setEstado("Edición agregada: " + anio + " - " + ciudad);
            formulario.cerrar();
        });
        formulario.mostrar();
    }
}
