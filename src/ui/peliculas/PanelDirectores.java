package ui.peliculas;

import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;
import ui.comun.PanelListado;

import java.util.List;

public class PanelDirectores extends PanelListado {

    public PanelDirectores(VentanaPrincipal ventana) {
        super(ventana, "Gestión de Directores", "Nombre", "Email", "Nacionalidad", "Películas");
        agregarBotones(ComponentesUI.crearBoton("Agregar director", this::agregarDirector));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return getSistema().getPersonaServicio().getDirectores().stream()
                .map(director -> new Object[]{
                        director.getNombre(), director.getEmail(), director.getNacionalidad(), director.cantidadPeliculas()})
                .toList();
    }

    private void agregarDirector() {
        String nombre = DialogoPersona.mostrar(getVentana(), "Agregar director",
                getSistema().getPersonaServicio()::registrarDirector);
        if (nombre == null) return;
        recargar();
        getVentana().setEstado("Director agregado: " + nombre);
    }
}
