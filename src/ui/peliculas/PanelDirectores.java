package ui.peliculas;

import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;
import ui.comun.PanelListado;

import java.util.List;

public class PanelDirectores extends PanelListado {
    private final PersonaServicio personaServicio;

    public PanelDirectores(VentanaPrincipal ventana, PersonaServicio personaServicio) {
        super(ventana, "Gestión de Directores", "Nombre", "Email", "Nacionalidad", "Películas");
        this.personaServicio = personaServicio;
        agregarBotones(ComponentesUI.crearBoton("Agregar director", this::agregarDirector));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return personaServicio.getDirectores().stream()
                .map(director -> new Object[]{
                        director.getNombre(), director.getEmail(), director.getNacionalidad(), director.cantidadPeliculas()})
                .toList();
    }

    private void agregarDirector() {
        DialogoPersona.mostrar(getVentana(), "Agregar director", personaServicio::registrarDirector)
                .ifPresent(director -> {
                    recargar();
                    getVentana().setEstado("Director agregado: " + director.getNombre());
                });
    }
}
